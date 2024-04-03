package com.hddev244.timetable.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.aspectj.weaver.patterns.ConcreteCflowPointcut.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hddev244.timetable.entity.DayEntity;
import com.hddev244.timetable.entity.LecturerEntity;
import com.hddev244.timetable.entity.PeriodEntity;
import com.hddev244.timetable.entity.RoomEntity;
import com.hddev244.timetable.entity.SlotEntity;
import com.hddev244.timetable.entity.SubjectEntity;
import com.hddev244.timetable.entity.SubjectOfGroupStudentEntity;
import com.hddev244.timetable.entity.SubjectOfLecturerEntity;
import com.hddev244.timetable.repository.LecturerRepository;
import com.hddev244.timetable.repository.SubjectOfGroupStudentRepository;
import com.hddev244.timetable.repository.SubjectOfLecturerRepository;

@Service
public class TimetableServiceImpl implements TimetableService {
    @Autowired
    SubjectOfGroupStudentRepository subjectOfGroupStudentRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    SubjectOfLecturerRepository subjectOfLecturerRepository;

    private Random random = new Random();
    List<SubjectOfGroupStudentEntity> subjectOfGroupStudents;
    List<LecturerEntity> lecturers;
    List<RoomEntity> rooms;
    List<DayEntity> days;
    List<PeriodEntity> periods;
    int totalPeriods = 0;
    SlotEntity[][][] timetable;

    // <id giảng viên, số ca đã dạy> luư số ca đã dạy của từng giảng viên , tăng khi
    // xếp giảng viên vào lớp, dùng để chia đều số ca cho giản viên.
    Map<String, Integer> countPeriodsTeached = new HashMap<>();

    // <id môn học, số ca còn lại> lưu số ca còn lại của từng môn, dùng để kiểm tra
    // số ca còn lại và xóa môn có thể dạy của giảng viên khi số ca này bằng 0
    Map<String, Integer> countPeriodsOfSubject = new HashMap<>();

    @Override
    public List<Optional<SlotEntity>> generateTimetable() {
        timetable = new SlotEntity[days.size()][periods.size()][rooms.size()];
        // 1. Khởi tạo số ca đã dạy của giảng viên
        lecturers.stream()
                .forEach(lecturer -> {
                    countPeriodsTeached.put(lecturer.getId(), 0);
                });

        // 2. khởi tạo dữ liệu số ca còn lại
        subjectOfGroupStudents.forEach(sogs -> {
            String subjectId = sogs.getSubject().getId();
            Integer count = countPeriodsOfSubject.get(subjectId);
            if (count == null)
                count = 0;
            countPeriodsOfSubject.put(subjectId, count + sogs.getSubject().getNumOfPeriods());
        });

        // 3. Thêm giảng viên vào danh sách lớp
        subjectOfGroupStudents.forEach(s -> {
            LecturerEntity lecturerFound = findLecturerForSubject(s.getSubject(), s.getSubject().getNumOfPeriods());
            if (lecturerFound != null) {
                s.setLecturer(lecturerFound);
            } else {

            }
        });

        // 4. Khởi tạo lịch học
        // duyệt qua tất cả các môn đã xếp cho lớp (SubjectOfGroupStudentEntity)
        int leng = subjectOfGroupStudents.size();
        int numofday = days.size();
        int numOfPeriods = periods.size();
        int numOfRoom = rooms.size();
        for (int x = 0; x < leng; x++) {
            boolean flag = false;
            SubjectOfGroupStudentEntity subjectOfClass = subjectOfGroupStudents.get(x);
            
            for (int i = 0; i < numofday && !flag; i++) {
                for (int j = 0; j < numOfPeriods && !flag; j++) {
                    for (int k = 0; k < numOfRoom && !flag; k++) {              
                        // Kiểm tra xem ca đã có trùng giảng viên không, nếu trùng thì tăng lên ca tiếp theo
                        if (timetable[i][j][k] == null && checkPeriodValidForLecturer(subjectOfClass.getLecturer(), i, j, numOfRoom)) {
                            for (int t = 0; t < subjectOfClass.getSubject().getNumOfPeriods(); t++) {
                                SlotEntity slot = new SlotEntity();
                                slot.setRoom(rooms.get(k));
                                slot.setPeriod(periods.get(j));
                                slot.setDay(days.get(i + t * 2));
                                timetable[i + t * 2][j][k] = slot;
                            }
                            flag = true; 
                        }
                    }
                }
            }
        }

        throw new UnsupportedOperationException("Unimplemented method 'generateTimetable'");
    }

    // phần 1.Tìm giảng viên phù hợp cho lớp - chia đều số ca
    private LecturerEntity findLecturerForSubject(SubjectEntity se, int numOfPeriods) {
        int minNumOfPeriods = minNumOfPefiods(this.countPeriodsTeached);
        // lọc giảng viên có số tiết đã dạy nhỏ nhất.
        List<LecturerEntity> lecturersFinding = subjectOfLecturerRepository.findBySubjectId(se.getId());
        lecturersFinding = lecturersFinding.stream()
                .filter(lecture -> countPeriodsTeached.get(lecture.getId()) <= minNumOfPeriods).toList();

        // Lọc Giảng viên có số môn có thể dạy nhỏ nhất
        int minSubjectCanTeach = minSubjectCanTeach(lecturersFinding);
        lecturersFinding = lecturersFinding.stream()
                .filter(lecture -> countPeriodsTeached
                        .get(lecture.getSubjectOfLecturerEntities().size()) <= minSubjectCanTeach)
                .toList();

        // trả về giảng viên ngẫu nhiên khi số giảng viên phù hợp lớn hơn 1.
        if (lecturersFinding.size() > 1) {
            int randomIndex = random.nextInt(lecturersFinding.size());

            Integer c = updateCountPeriodsOfSubject(se, numOfPeriods); // kiểm tra số ca còn lại của môn
            // xóa môn có thể dạy của giảng viên nếu số tiết còn lại của môn đó = 0
            if (c <= 0) {
                lecturers.forEach(lecturer -> {
                    Iterator<SubjectOfLecturerEntity> iterator = lecturer.getSubjectOfLecturerEntities().iterator();
                    while (iterator.hasNext()) {
                        SubjectOfLecturerEntity subjecOfLecturer = iterator.next();
                        if (se.getId().equals(subjecOfLecturer.getId())) {
                            iterator.remove();
                        }
                    }
                });
            }
            return lecturersFinding.get(randomIndex);
        } else if (lecturersFinding.size() == 1) {
            return lecturersFinding.get(0);
        }
        return null;
    }

    // Tìm số tiết đã dạy nhỏ nhất của tất cả giảng viên
    private Integer minNumOfPefiods(Map<String, Integer> c) {
        return c.values().stream()
                .min(Integer::compareTo)
                .orElse(0);
    }

    // Tìm số môn có thể dạy nhỏ nhất của tất cả giảng viên
    private Integer minSubjectCanTeach(List<LecturerEntity> l) {
        return l.stream()
                .mapToInt(lecturer -> lecturer.getSubjectOfLecturerEntities().size())
                .min()
                .orElse(0); // If the list is empty, return 0
    }

    // cập nhật số ca còn lại theo môn
    private Integer updateCountPeriodsOfSubject(SubjectEntity s, int numOfPeriods) {
        Integer count = this.countPeriodsOfSubject.get(s.getId());
        this.countPeriodsOfSubject.put(s.getId(), count - numOfPeriods);
        return count - numOfPeriods;
    }

    // Phần 2 .
    // kiểm tra giảng viên có trùng trong ca được set không
    private boolean checkPeriodValidForLecturer(LecturerEntity lecturer, int day, int period, int numOfRoom) {
        for (int i = 0; i < numOfRoom; i++) {
            SlotEntity slot = timetable[day][period][i];
            if (slot != null) {
                if (slot.getSubjectOfGroupStudent().getLecturer().getId().equals(lecturer.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

}
