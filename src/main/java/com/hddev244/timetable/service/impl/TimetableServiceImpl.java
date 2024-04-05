package com.hddev244.timetable.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hddev244.timetable.entity.DayEntity;
import com.hddev244.timetable.entity.GroupStudentEntity;
import com.hddev244.timetable.entity.LecturerEntity;
import com.hddev244.timetable.entity.PeriodEntity;
import com.hddev244.timetable.entity.RoomEntity;
import com.hddev244.timetable.entity.SlotEntity;
import com.hddev244.timetable.entity.SubjectEntity;
import com.hddev244.timetable.entity.SubjectOfGroupStudentEntity;
import com.hddev244.timetable.entity.SubjectOfLecturerEntity;
import com.hddev244.timetable.repository.DayRepository;
import com.hddev244.timetable.repository.LecturerRepository;
import com.hddev244.timetable.repository.PeriodRepository;
import com.hddev244.timetable.repository.RoomRepository;
import com.hddev244.timetable.repository.SubjectOfGroupStudentRepository;
import com.hddev244.timetable.repository.SubjectOfLecturerRepository;
import com.hddev244.timetable.service.TimetableService;

@Service
public class TimetableServiceImpl implements TimetableService {
    @Autowired
    SubjectOfGroupStudentRepository subjectOfGroupStudentRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    SubjectOfLecturerRepository subjectOfLecturerRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    DayRepository dayRepository;
    @Autowired
    PeriodRepository periodRepository;

    private Random random = new Random();

    List<SubjectOfGroupStudentEntity> subjectOfGroupStudents; // danh sách môn học của tất cả các lơp trong 1 block
    List<LecturerEntity> lecturers; // danh sách giảng viên
    List<RoomEntity> rooms; // danh sách phòng học
    List<DayEntity> days; // danh sách ngày học trong tuần
    List<PeriodEntity> periods; // danh sách ca học trong ngày
    SlotEntity[][][] timetable;// lịch học

    // <id giảng viên, số ca đã dạy> luư số ca đã dạy của từng giảng viên , tăng khi
    // xếp giảng viên vào lớp, dùng để chia đều số ca cho giản viên.
    Map<String, Integer> countPeriodsTeached = new HashMap<>();

    // <id môn học, số ca còn lại> lưu số ca còn lại của từng môn, dùng để kiểm tra
    // số ca còn lại và xóa môn có thể dạy của giảng viên khi số ca này bằng 0
    Map<String, Integer> countPeriodsOfSubject = new HashMap<>();

    @Override
    public SlotEntity[][][] generateTimetable() {
        int totalPeriods = 0; // tổng số phải xếp
        int totalPeriodsAdded = 0; // tổng số đã xếp

        // lấy danh sách môn học của tất cả các lớp trong 1 block từ database
        subjectOfGroupStudents = subjectOfGroupStudentRepository.findByBlockIdAndSemesterId(1L, 1L);
        lecturers = lecturerRepository.findAll();
        rooms = roomRepository.findAll();
        days = dayRepository.findAll();
        periods = periodRepository.findAll();
        timetable = new SlotEntity[days.size()][periods.size()][rooms.size()];


        // 1. Khởi tạo số ca đã dạy của giảng viên
        lecturers.stream()
                .forEach(lecturer -> {
                    countPeriodsTeached.put(lecturer.getId(), 0);
                });

        // 2. khởi tạo dữ liệu số ca còn lại của môn
        subjectOfGroupStudents.forEach(sogs -> {
            String subjectId = sogs.getSubject().getId();
            Integer count = countPeriodsOfSubject.get(subjectId);
            if (count == null) {
                count = 0;
            }
            countPeriodsOfSubject.put(subjectId, count + sogs.getSubject().getNumOfPeriods());
        });

        // tính tổng số ca phải xếp
        totalPeriods = subjectOfGroupStudents.stream()
                .mapToInt(s -> s.getSubject().getNumOfPeriods())
                .sum();

        System.out.println("Total periods: " + totalPeriods);

        // xóa môn có thể day của giảng viên nếu môn học không nằm trong
        // subjectOfGroupStudents
        lecturers = removeSubjectCanTeach(subjectOfGroupStudents, lecturers);

        lecturers.forEach(lecturer -> {
            System.out.println("Lecturer: " + lecturer.getId() + " - " + lecturer.getName() + " - "
                    + lecturer.getSubjectOfLecturerEntities().size());
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~");
        });

        // 3. Thêm giảng viên vào danh sách lớp
        subjectOfGroupStudents.forEach(s -> {
            LecturerEntity lecturerFound = findLecturerForSubject(lecturers, s.getSubject(),
                    s.getSubject().getNumOfPeriods());
            if (lecturerFound != null) {
                s.setLecturer(lecturerFound);
                countPeriodsTeached.put(lecturerFound.getId(),
                        countPeriodsTeached.get(lecturerFound.getId()) + s.getSubject().getNumOfPeriods());
            }
        });

        // System.out.println("Lecture : " + subjectOfGroupStudents.get(0).getLecturer()
        // + "\n");

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
                        // Kiểm tra xem ca đã có trùng giảng viên không, nếu trùng thì tăng lên ca tiếp
                        // theo
                        if (timetable[i][j][k] == null
                                && checkPeriodValidForLecturer(subjectOfClass.getLecturer(), i, j, numOfRoom)
                                && checkperiodValidForGroupStudent(subjectOfClass.getGroupStudent(), i, j, numOfRoom)) {
                            for (int t = 0; t < subjectOfClass.getSubject().getNumOfPeriods(); t++) {
                                SlotEntity slot = new SlotEntity();
                                slot.setRoom(rooms.get(k));
                                slot.setPeriod(periods.get(j));
                                slot.setDay(days.get(i + t * 2));
                                slot.setSubjectOfGroupStudent(subjectOfClass);
                                timetable[i + t * 2][j][k] = slot;
                                totalPeriodsAdded++;
                            }
                            flag = true;
                        }
                    }
                }
            }
        }
        System.out.println("Total periods added: " + totalPeriodsAdded);
        // In ra số ca đã dạy của từng giảng viên
        countPeriodsTeached.forEach(
                (key, value) -> {
                    System.out.println("Lecturer: " + key + " - Num of periods: " + value);
                    System.out.println("_____________");
                });


        System.out.println(
                "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("");
        // Print the header
        System.out.printf("%-15s", "");
        for (int k = 0; k < numOfRoom; k++) {
            System.out.printf("| %-20s", "Room " + (k + 1));
        }
        System.out.println("|");
        System.out.println(
                "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        // Print the timetable
        for (int i = 0; i < numofday; i++) {
            for (int j = 0; j < numOfPeriods; j++) {
                System.out.printf("%-15s", "Day" + (i + 1) + "-Period" + (j + 1));
                for (int k = 0; k < numOfRoom; k++) {
                    SlotEntity slot = timetable[i][j][k];
                    if (slot != null && slot.getSubjectOfGroupStudent() != null
                            && slot.getSubjectOfGroupStudent().getLecturer() != null) {
                        System.out.printf("| %-20s", slot.getSubjectOfGroupStudent().getLecturer().getId() + " - "
                                + slot.getSubjectOfGroupStudent().getGroupStudent().getId());
                    } else {
                        System.out.printf("| %-20s", "");
                    }
                }
                System.out.println("|");
            }
            System.out.println(
                    "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }
        List<Optional<SlotEntity>> result = new ArrayList<Optional<SlotEntity>>();
        for (int i = 0; i < numofday; i++) {
            for (int j = 0; j < numOfPeriods; j++) {
                for (int k = 0; k < numOfRoom; k++) {
                    SlotEntity slot = timetable[i][j][k];
                    if (slot != null) {
                        result.add(Optional.of(slot));
                    } else {
                        result.add(Optional.empty());
                    }
                }
            }
        }

        // convert timetable to easier to read format
        timetable = convertTimetable(timetable);

        //tạo response
        Map<String, Object> response = new HashMap<>();
        response.put("timetable", timetable);

        return timetable;
    }

    // phần 1.Tìm giảng viên phù hợp cho lớp - chia đều số ca
    private LecturerEntity findLecturerForSubject(List<LecturerEntity> lecturers, SubjectEntity se, int numOfPeriods) {
        // lọc giảng viên có số ca đã dạy nhỏ nhất.
        List<LecturerEntity> lecturersFinding = subjectOfLecturerRepository.findBySubjectId(se.getId());

        int minNumOfPeriods = minNumOfPefiods(lecturersFinding, this.countPeriodsTeached);
        // lọc giảng viên có số tiết đã dạy nhỏ nhất
        lecturersFinding = lecturersFinding.stream()
                .filter(lecture -> countPeriodsTeached.get(lecture.getId()) <= minNumOfPeriods)
                .toList();

        // // Lọc Giảng viên có số môn có thể dạy nhỏ nhất
        int minSubjectCanTeach = minSubjectCanTeach(lecturersFinding);
        lecturersFinding = lecturersFinding.stream()
                .filter(lecture -> lecture.getSubjectOfLecturerEntities().size() <= minSubjectCanTeach)
                .toList();

        // // trả về giảng viên ngẫu nhiên khi số giảng viên phù hợp lớn hơn 1.
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
    private Integer minNumOfPefiods(List<LecturerEntity> lecturers, Map<String, Integer> c) {
        return lecturers.stream()
                .mapToInt(lecturer -> c.get(lecturer.getId()))
                .min()
                .orElse(0); // If the list is empty, return 0
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
    // private boolean checkPeriodValidForLecturer(LecturerEntity lecturer, int day,
    // int period, int numOfRoom) {
    // for (int i = 0; i < numOfRoom; i++) {
    // SlotEntity slot = timetable[day][period][i];
    // System.out.println("slot: " + slot);
    // if (slot != null) {
    // if
    // (slot.getSubjectOfGroupStudent().getLecturer().getId().equals(lecturer.getId()))
    // {
    // return false;
    // }
    // }
    // }
    // return true;
    // }
    private boolean checkPeriodValidForLecturer(LecturerEntity lecturer, int day, int period, int numOfRoom) {
        for (int i = 0; i < numOfRoom; i++) {
            SlotEntity slot = timetable[day][period][i];
            if (slot != null) {
                SubjectOfGroupStudentEntity subjectOfGroupStudent = slot.getSubjectOfGroupStudent();
                if (subjectOfGroupStudent != null
                        && subjectOfGroupStudent.getLecturer().getId().equals(lecturer.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkperiodValidForGroupStudent(GroupStudentEntity groupStudent, int day, int period,
            int numOfRoom) {
        for (int i = 0; i < numOfRoom; i++) {
            SlotEntity slot = timetable[day][period][i];
            if (slot != null) {
                SubjectOfGroupStudentEntity subjectOfGroupStudent = slot.getSubjectOfGroupStudent();
                if (subjectOfGroupStudent != null
                        && subjectOfGroupStudent.getGroupStudent().getId().equals(groupStudent.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    public SlotEntity[][][] convertTimetable(SlotEntity[][][] timetable) {
        int numOfDays = timetable.length;
        int numOfPeriods = timetable[0].length;
        int numOfRooms = timetable[0][0].length;

        SlotEntity[][][] newTimetable = new SlotEntity[numOfDays][numOfRooms][numOfPeriods];

        for (int day = 0; day < numOfDays; day++) {
            for (int room = 0; room < numOfRooms; room++) {
                for (int period = 0; period < numOfPeriods; period++) {
                    newTimetable[day][room][period] = timetable[day][period][room];
                }
            }
        }

        return newTimetable;
    }

    // xóa môn có thể dạy của giảng viên nếu môn học không nằm trong subjectOfGroupStudents
    public List<LecturerEntity> removeSubjectCanTeach(List<SubjectOfGroupStudentEntity> subjectOfGroupStudents,
            List<LecturerEntity> lecturers) {
        if (lecturers == null) {
            return null;
        }
        if (subjectOfGroupStudents == null) {
            return lecturers;
        }


        List<String> subjectsIds = subjectOfGroupStudents.stream()
                .map(sogs -> sogs.getSubject().getId())
                .toList();
        System.out.println("subjectsId: " + subjectsIds.size());

        lecturers.stream().forEach(lecturer -> {
            List<SubjectOfLecturerEntity> se = lecturer.getSubjectOfLecturerEntities();
           try {
             if (!se.isEmpty()) {
                 int leng = se.size();
                    for (int i = 0; i < leng; i++) {
                        if (!subjectsIds.contains(se.get(i).getSubject().getId())) {
                            se.remove(i);
                        }
                    }
                 lecturer.setSubjectOfLecturerEntities(se);
             }
           } catch (Exception e) {
            // TODO: handle exception
           }
        });
        return lecturers;
    }
}
