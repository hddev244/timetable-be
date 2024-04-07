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

        System.out.println("Tổng số slot phải xếp: " + totalPeriods);

        // xóa môn có thể day của giảng viên nếu môn học không nằm trong
        // subjectOfGroupStudents
        lecturers = removeSubjectCanTeach(subjectOfGroupStudents, lecturers);


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
        int numOfday = days.size();
        int numOfPeriods = periods.size();
        int numOfRoom = rooms.size();
        int maxSlotOfPeriod = numOfRoom*7/10;
        System.out.println("Số slot lớn nhất Của ca 3 (70% phòng): " + maxSlotOfPeriod);
        while (totalPeriodsAdded < totalPeriods) {
            totalPeriodsAdded = 0;
            for (int p = 3; p > 0; p--) {
                for (int x = 0; x < leng; x++) {
                    boolean flag = false;
                    SubjectOfGroupStudentEntity subjectOfClass = subjectOfGroupStudents.get(x);
                    if (subjectOfClass.getSubject().getNumOfPeriods() != p) {
                        continue;
                    }

                    for (int i = 0; i < numOfday && !flag; i++) {

                        // kiểm tra nếu lớp đã trùng trong ngày thì chyển sang ngày tiếp theo
                        if (p == 3) {
                            boolean check = false;
                            String groupStudentId = subjectOfClass.getGroupStudent().getId();
                            for (int j = 0; j < numOfPeriods; j++) {
                                for (int k = 0; k < numOfRoom; k++) {
                                    SlotEntity slot = timetable[i][j][k];
                                    if (slot != null) {
                                        if (slot.getSubjectOfGroupStudent().getGroupStudent().getId()
                                                .equals(groupStudentId)) {
                                            check = true;
                                            break;
                                        }
                                    }
                                    if (check) {
                                        break;
                                    }
                                }
                            }
                            if (check) {
                                continue;
                            }
                        }

                        for (int j = 0; j < numOfPeriods && !flag; j++) {       
                            if(j == 2){
                                int countSlotOfThisPeriod = 0;
                                for(int r = 0; r < numOfRoom; r++){
                                    if(timetable[i][j][r] != null){
                                        countSlotOfThisPeriod++;
                                    }
                                }
                                if(countSlotOfThisPeriod >= maxSlotOfPeriod){
                                    continue;
                                }
                            }
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
        int s = evaluate(timetable);
        int score = s;

        // 6. Đổi chỗ slot nếu có thể
        int loop = 1000;
        while (loop > 0) {
            for(int rr = 0; rr < numOfRoom ; rr++){
                for(int dd = 0; dd < numOfday ; dd++){
                    TimetabeScore ts = evolurionByRoom(timetable, numOfday, numOfRoom, numOfPeriods, dd, rr);
                    if(ts.getScore() > score){
                        score = ts.getScore();
                        timetable = ts.getTimetable();
                    }    
                }
            }
            System.out.println("current scores timetable: " + score);
            loop--;
        }
       

        System.out.println("Score of first timetable: " + s);

        System.out.println("Total periods added: " + totalPeriodsAdded);
        // In ra số ca đã dạy của từng giảng viên
        countPeriodsTeached.forEach(
        (key, value) -> {
        System.out.println("Lecturer: " + key + " - Num of periods: " + value);
        System.out.println("_____________");
        });

        // 7. Thêm thông tin phòng và ca học vào lịch học
        for (int i = 0; i < numOfday; i++) {
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
            int day,int period,  int room1, int room2) {

        SlotEntity[][][] newTimetable = timetable;
        TimetabeScore result = new TimetabeScore(timetable, currentScore);

        // kiểm tra xem slot có hợp lệ không
        if (day < 0 || day >= 6 ) {
            return result;
        }

        SlotEntity slot1 = timetable[day][period][room1];
        SlotEntity slot2 = timetable[day][period][room2];
        int maxNumOfPeriods = 0;
        if (slot1 == null && slot2 == null) {
            return result;
        }

        if (slot1 != null && slot2 != null) {
            maxNumOfPeriods = Math.max(slot1.getSubjectOfGroupStudent().getSubject().getNumOfPeriods(),
                    slot2.getSubjectOfGroupStudent().getSubject().getNumOfPeriods());
        } else if (slot1 != null) {
            maxNumOfPeriods = slot1.getSubjectOfGroupStudent().getSubject().getNumOfPeriods();
        } else {
            maxNumOfPeriods = slot2.getSubjectOfGroupStudent().getSubject().getNumOfPeriods();
        }

        // đổi chỗ slot
        if(day < 2){
            for (int i = 0; i < maxNumOfPeriods; i++) {
                newTimetable[day][period][room1] = slot2;
                newTimetable[day][period][room2] = slot1;
            }
            int newScore = evaluate(newTimetable);
            if (newScore > currentScore) {
                result.setTimetable(newTimetable);
                result.setScore(newScore);
            }

        }

        return result;
    }

    private TimetabeScore evolurionByRoom(SlotEntity[][][] timetable,int totalDays, int totalRooms,  int totalPeriods,int day, int room) {
        SlotEntity[][][] newTimetable = timetable;
        TimetabeScore result = new TimetabeScore(timetable, evaluate(timetable));

        Map<String, Integer> countPeriodsOfLecturers = new HashMap<>();
        
        // lấy tất cả slot trong 1 phòng của 1 ngày
        for(int p = 0; p < totalPeriods; p++){
            SlotEntity slot = timetable[day][p][room];
            if(slot != null){
                String lecturerId = slot.getSubjectOfGroupStudent().getLecturer().getId();
                Integer count = countPeriodsOfLecturers.get(lecturerId);
                if(count == null){
                    count = 0;
                }
                countPeriodsOfLecturers.put(lecturerId, count+1);
            }
        }

        // lấy ra giảng viên có số ca nhiều nhất 
        String lecturerId = "";
        int max = 0;
        for(Map.Entry<String, Integer> entry : countPeriodsOfLecturers.entrySet()){
            if(entry.getValue() > max){
                max = entry.getValue();
                lecturerId = entry.getKey();
            }
        }

        // Tìm period không phải của giảng viên đó. nếu tìm thấy thìm giảng viên đó ở phòng khác cùng ca học, nếu tìm thấy thì đổi chỗ, nếu không thì sang ca tiếp theo
        for(int p = 0; p < totalPeriods; p++){
            SlotEntity slot = timetable[day][p][room];
            if(slot != null){
                // lấy số ca trong tuần của môn học trong slot tìm được
                int numOfPeriodsOfThisSlot = slot.getSubjectOfGroupStudent().getSubject().getNumOfPeriods();

                if(!slot.getSubjectOfGroupStudent().getLecturer().getId().equals(lecturerId)){
                    for(int r = 0; r < totalRooms; r++){
                        SlotEntity slotFound = timetable[day][p][r];
                        if(slotFound != null){
                            if(slotFound.getSubjectOfGroupStudent().getLecturer().getId().equals(lecturerId)){
                                for(int i = 0; i < numOfPeriodsOfThisSlot; i++){
                                    newTimetable[day][p][room] = slotFound;
                                    newTimetable[day][p][r] = slot;
                                }
                                int newScore = evaluate(newTimetable);
                                if(newScore >= result.getScore()){
                                    result.setTimetable(newTimetable);
                                    result.setScore(newScore);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    

    /**
     * Đánh giá điểm của thời khóa biểu
     * 
     * @param timetable // thời khóa biểu cần đánh giá
     * @return // điểm của thời khóa biểu
     */
    private int evaluate(SlotEntity[][][] timetable) {
        int score = 0;
        int numOfDays = timetable.length;
        int numOfPeriods = 6;
        int numOfRooms = timetable[0][0].length;

        for (int room = 0; room < numOfRooms; room++) {
            for (int day = 0; day < numOfDays; day++) {
                score += evaluateOneRoom(timetable, day, numOfPeriods, room);
                }
            }
        return score;
    }

    private int evaluateOneRoom(SlotEntity[][][] timetable, int day, int numOfPeriods, int room) {
        int score = 0;

        List<String> lecturersIdChecked = new ArrayList<>();  
        for(int i = 0 ; i < numOfPeriods ; i++){
            SlotEntity slot = timetable[day][i][room];
            if(slot != null){
                String lecturerId = slot.getSubjectOfGroupStudent().getLecturer().getId();
                if(lecturersIdChecked.contains(lecturerId)){
                    score++;
                } else {
                    lecturersIdChecked.add(lecturerId);
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
