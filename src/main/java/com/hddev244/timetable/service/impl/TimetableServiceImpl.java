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
        SlotEntity[][][] timetable;// lịch học

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
        while (totalPeriodsAdded < totalPeriods) {
            totalPeriodsAdded = 0;
            for (int p = 3; p > 0; p--) {
                for (int x = 0; x < leng; x++) {
                    boolean flag = false;
                    SubjectOfGroupStudentEntity subjectOfClass = subjectOfGroupStudents.get(x);
                    if (subjectOfClass.getSubject().getNumOfPeriods() != p) {
                        continue;
                    }
                    for (int i = 0; i < numofday && !flag; i++) {
                        for (int j = 0; j < numOfPeriods && !flag; j++) {
                            for (int k = 0; k < numOfRoom && !flag; k++) {
                                // Kiểm tra xem ca đã có trùng giảng viên hoặc lớp không, nếu trùng thì tăng lên
                                // ca tiếp
                                // theo
                                if (timetable[i][j][k] == null
                                        && checkPeriodValidForLecturer(timetable, subjectOfClass.getLecturer(), i, j,
                                                numOfRoom)
                                        && checkperiodValidForGroupStudent(timetable, subjectOfClass.getGroupStudent(),
                                                i, j,
                                                numOfRoom)) {
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
            }
        }

        // 5. Đánh giá lịch học
        int[] s = evaluate(timetable);
        int score = s[0] + s[1];

        // 6. Đổi chỗ slot nếu có thể
        int loop = 0;
        while (loop < 0) {
            int day1 = random.nextInt(2);
            int day2 = random.nextInt(2);
            int period1 = random.nextInt(numOfPeriods);
            int room1 = random.nextInt(numOfRoom);
            int period2 = random.nextInt(numOfPeriods);
            int room2 = random.nextInt(numOfRoom);
            TimetabeScore result = changeSlotOfTimetable(timetable, score, day1, period1, room1, day1,
                    period2, room2);
            timetable = result.getTimetable();
            score = result.getScore();

            System.out.println("Change slot score: " + score);
            System.out.println("Loop: " + loop);
            System.out.println("________________________");

            loop++;
        }

        System.out.println("Score of first timetable: " + (s[0] + s[1]));

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
                        System.out.printf("| %-20s", slot.getSubjectOfGroupStudent().getLecturer().getId() + "-"
                                + slot.getSubjectOfGroupStudent().getGroupStudent().getId()
                                + "-" + slot.getSubjectOfGroupStudent().getSubject().getNumOfPeriods());
                    } else {
                        System.out.printf("| %-20s", "");
                    }
                }
                System.out.println("|");
            }
            System.out.println(
                    "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }
        for (int i = 0; i < numofday; i++) {
            for (int j = 0; j < numOfPeriods; j++) {
                for (int k = 0; k < numOfRoom; k++) {
                    SlotEntity slot = timetable[i][j][k];
                    if (slot != null) {

                        timetable[i][j][k].setRoom(rooms.get(k));
                        timetable[i][j][k].setPeriod(periods.get(j));
                        timetable[i][j][k].setDay(days.get(i));
                    } else {
                        SlotEntity slot1 = new SlotEntity();
                        slot1.setRoom(rooms.get(k));
                        slot1.setPeriod(periods.get(j));
                        slot1.setDay(days.get(i));
                        timetable[i][j][k] = slot1;
                    }
                }
            }
        }

        // convert timetable to easier to read format
        timetable = convertTimetable(timetable);

        // tạo response
        Map<String, Object> response = new HashMap<>();
        response.put("timetable", timetable);
        response.put("totalPeriods", totalPeriods);
        response.put("totalPeriodsAdded", totalPeriodsAdded);
        // thêm phòng và ca học vào lịch học

        return timetable;
    }

    /**
     * all functions below are used in the generateTimetable function
     */

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
    private boolean checkPeriodValidForLecturer(SlotEntity[][][] timetable, LecturerEntity lecturer, int day,
            int period, int numOfRoom) {
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

    // kiểm tra lớp có trùng trong ca được set không
    private boolean checkperiodValidForGroupStudent(SlotEntity[][][] timetable, GroupStudentEntity groupStudent,
            int day, int period,
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

    // xóa môn có thể dạy của giảng viên nếu môn học không nằm trong
    // subjectOfGroupStudents
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

    /**
     * Kiểm tra slot có thể đổi chỗ không, nếu có thì đổi chỗ và đánh giá điểm của
     * lịch mới,
     * nếu điểm mới tốt hơn thì chấp nhận lịch mới, ngược lại giữ nguyên lịch cũ.
     * 
     * @param timetable // thời khóa biểu cần đôi chỗ
     * @param day       // ngày của slots
     * @param period1   // ca của slot 1
     * @param room1     // phòng của slot 1
     * @param period2   // ca của slot 2
     * @param room2     // phòng của slot 2
     * @return // thời khóa biểu mới sau khi đổi chỗ nếu điểm mới tốt hơn hoặc giữ
     *         nguyên thời khóa biểu cũ nếu điểm mới không tốt hơn.
     */

    private TimetabeScore changeSlotOfTimetable(
            SlotEntity[][][] timetable,
            int currentScore,
            int day1, int period1, int room1,
            int day2, int period2, int room2) {
        SlotEntity[][][] newTimetable = timetable;
        TimetabeScore result = new TimetabeScore(timetable, currentScore);

        // kiểm tra xem slot có hợp lệ không
        if (day1 < 0 || day1 >= 2 || day2 < 0 || day2 >= 2) {
            return result;
        }

        try {

            // if (timetable[day1][period1][room1] == null && timetable[day2][period2][room2] != null) {
            //     int numOfPeriods2 = timetable[day2][period2][room2].getSubjectOfGroupStudent().getSubject()
            //             .getNumOfPeriods();
            //     for (int i = 0; i < numOfPeriods2; i++) {
            //         SlotEntity slot2 = newTimetable[day2 + (2 * i)][period2][room2];

            //         LecturerEntity lecturer2 = slot2.getSubjectOfGroupStudent().getLecturer();
            //         GroupStudentEntity class2 = slot2.getSubjectOfGroupStudent().getGroupStudent();

            //         if (slot2 != null) {
            //             if (checkperiodValidForGroupStudent(newTimetable, class2, day1 + (2 * i), period1, room1)
            //                     && checkPeriodValidForLecturer(newTimetable, lecturer2, day1 + (2 * i), period1,
            //                             room1)) {
            //                 newTimetable[day1 + (2 * i)][period1][room1] = slot2;
            //                 newTimetable[day2 + (2 * i)][period2][room2] = null;
            //             } else {
            //                 return new TimetabeScore(timetable, currentScore);
            //             }

            //         } else {
            //             return new TimetabeScore(timetable, currentScore);
            //         }
            //     }
            //     // Đánh giá điểm của thời khóa biểu mới
            //     int[] newScore = evaluate(newTimetable);
            //     int newScoreValue = newScore[0] + newScore[1];

            //     System.out.println("New score: " + newScoreValue);
            //     if (newScoreValue < currentScore) {
            //         return new TimetabeScore(newTimetable, newScoreValue);
            //     } else {
            //         return new TimetabeScore(timetable, currentScore);
            //     }
            // }

            // if (timetable[day1][period1][room1] != null && timetable[day2][period2][room2] == null) {
            //     int numOfPeriods1 = timetable[day1][period1][room1].getSubjectOfGroupStudent().getSubject()
            //             .getNumOfPeriods();
            //     for (int i = 0; i < numOfPeriods1; i++) {
            //         SlotEntity slot1 = newTimetable[day1 + (2 * i)][period1][room1];

            //         LecturerEntity lecturer1 = slot1.getSubjectOfGroupStudent().getLecturer();
            //         GroupStudentEntity class1 = slot1.getSubjectOfGroupStudent().getGroupStudent();

            //         if (slot1 != null) {
            //             if (checkperiodValidForGroupStudent(newTimetable, class1, day2 + (2 * i), period2, room2)
            //                     && checkPeriodValidForLecturer(newTimetable, lecturer1, day2 + (2 * i), period2,
            //                             room2)) {

            //                 newTimetable[day2 + (2 * i)][period2][room2] = slot1;
            //                 newTimetable[day1 + (2 * i)][period1][room1] = null;
            //             } else {
            //                 return new TimetabeScore(timetable, currentScore);
            //             }
            //         } else {
            //             return new TimetabeScore(timetable, currentScore);
            //         }
            //     }
            //     // Đánh giá điểm của thời khóa biểu mới
            //     int[] newScore = evaluate(newTimetable);
            //     int newScoreValue = newScore[0] + newScore[1];

            //     System.out.println("New score: " + newScoreValue);
            //     if (newScoreValue < currentScore) {
            //         return new TimetabeScore(newTimetable, newScoreValue);
            //     } else {
            //         return new TimetabeScore(timetable, currentScore);
            //     }
            // }

            if (timetable[day1][period1][room1] == null && timetable[day2][period2][room2] == null) {
                return result;
            }

            int numOfPeriods1 = timetable[day1][period1][room1].getSubjectOfGroupStudent().getSubject()
                    .getNumOfPeriods();
            int numOfPeriods2 = timetable[day2][period2][room2].getSubjectOfGroupStudent().getSubject()
                    .getNumOfPeriods();

            int maxNumOfPeriods = Math.max(numOfPeriods1, numOfPeriods2);
            System.out.println("Max number of periods: " + maxNumOfPeriods);

            for (int i = 0; i < maxNumOfPeriods; i++) {
                SlotEntity slot1 = newTimetable[day1 + (2 * i)][period1][room1];
                SlotEntity slot2 = newTimetable[day2 + (2 * i)][period2][room2];

                LecturerEntity lecturer1 = slot1.getSubjectOfGroupStudent().getLecturer();
                LecturerEntity lecturer2 = slot2.getSubjectOfGroupStudent().getLecturer();

                GroupStudentEntity class1 = slot1.getSubjectOfGroupStudent().getGroupStudent();
                GroupStudentEntity class2 = slot2.getSubjectOfGroupStudent().getGroupStudent();

                if (slot1 != null && slot2 != null) {
                    if (checkPeriodValidForLecturer(newTimetable, lecturer1, day2 + (2 * i), period2, room2)
                            && checkPeriodValidForLecturer(newTimetable, lecturer2, day1 + (2 * i), period1, room1)
                            && checkperiodValidForGroupStudent(newTimetable, class1, day2 + (2 * i), period2, room2)
                            && checkperiodValidForGroupStudent(newTimetable, class2, day1 + (2 * i), period1, room1)) {
                        // đổi thông tin room. period của slot1 và slot2
                        RoomEntity tempRoom = slot1.getRoom();
                        slot1.setRoom(slot2.getRoom());
                        slot2.setRoom(tempRoom);

                        PeriodEntity tempPeriod = slot1.getPeriod();
                        slot1.setPeriod(slot2.getPeriod());
                        slot2.setPeriod(tempPeriod);

                        // Đổi chỗ slot1 và slot2
                        newTimetable[day1 + (2 * i)][period1][room1] = slot2;
                        newTimetable[day2 + (2 * i)][period2][room2] = slot1;
                        System.out.println("Change slot: " + i + " - " + "day" + day1 + " - " + "period 1" + period1
                                + " - " + room1 + " - "
                                + "day " + day2 + "- period 2" + period2 + " - " + room2);

                        // SlotEntity[][][] tem2 = new
                    } else {
                        return new TimetabeScore(timetable, currentScore);
                    }
                } else {
                    return new TimetabeScore(timetable, currentScore);
                }
            }
            // Đánh giá điểm của thời khóa biểu mới
            int[] newScore = evaluate(newTimetable);
            int newScoreValue = newScore[0] + newScore[1];

            System.out.println("New score: " + newScoreValue);
            if (newScoreValue < currentScore) {
                return new TimetabeScore(newTimetable, newScoreValue);
            } else {
                return new TimetabeScore(timetable, currentScore);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return new TimetabeScore(timetable, currentScore);
        }
    }

    /**
     * Đánh giá điểm của thời khóa biểu
     * 
     * @param timetable // thời khóa biểu cần đánh giá
     * @return // điểm của thời khóa biểu
     */
    private int[] evaluate(SlotEntity[][][] timetable) {
        int[] score = { 0, 0 };
        int numOfDays = timetable.length;
        int numOfPeriods = timetable[0].length;
        int numOfRooms = timetable[0][0].length;
        List<String> slotChecked = new ArrayList<>();

        for (int day = 0; day < numOfDays; day++) {
            for (int period = 0; period < numOfPeriods; period++) {
                for (int room = 0; room < numOfRooms; room++) {
                    SlotEntity slot = timetable[day][period][room];
                    if (slot != null) {
                        // kiểm tra slot đã kiểm tra chưa (kiểm tra theo lớp học và giảng viên)
                        String key = slot.getSubjectOfGroupStudent().getLecturer().getId() + day;
                        if (!slotChecked.contains(key)) {
                            // score += evaluateOneSlot(timetable, slot.getSubjectOfGroupStudent().getId());
                            int[] subjectScore = evaluateOneDay(timetable, day, period, room);
                            score[0] += subjectScore[0];
                            score[1] += subjectScore[1];
                            // thêm slot vào danh sách đã kiểm tra
                            slotChecked.add(key);
                        }
                    }
                }
            }
        }
        return score;
    }

    private int[] evaluateOneDay(SlotEntity[][][] timetable, int day, int period, int room) {
        int[] score = { 0, 0 };
        int numOfDays = timetable.length;
        int numOfPeriods = timetable[0].length;
        int numOfRooms = timetable[0][0].length;
        SlotEntity currentSlot = timetable[day][period][room];
        int currentPeriod = period;
        int currentRoom = room;

        for (int p = 0; period < numOfPeriods; period++) {
            for (int r = 0; room < numOfRooms; room++) {
                SlotEntity slot = timetable[day][period][room];
                if (slot != null) {
                    String currentKey = currentSlot.getSubjectOfGroupStudent().getLecturer().getId();
                    String key = slot.getSubjectOfGroupStudent().getLecturer().getId();

                    if (currentKey.equals(key)) {
                        // score += evaluateOneSlot(timetable, slot.getSubjectOfGroupStudent().getId());
                        int periodScore = Math.abs(currentPeriod - p);
                        int roomScore = Math.abs(currentRoom - r);

                        score[0] += periodScore;
                        // score[1] += roomScore;

                        currentSlot = slot;
                        currentPeriod = p;
                        currentRoom = r;
                    }

                }
            }
        }
        return score;
    }

    //
    private class TimetabeScore {
        private SlotEntity[][][] timetable;
        private int score;

        public TimetabeScore(SlotEntity[][][] timetable, int score) {
            this.timetable = timetable;
            this.score = score;
        }

        public SlotEntity[][][] getTimetable() {
            return timetable;
        }

        public void setTimetable(SlotEntity[][][] timetable) {
            this.timetable = timetable;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
