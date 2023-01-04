package com.junho;

import com.junho.constant.DayOfWeek;
import com.junho.constant.PrivateInfo;
import org.kohsuke.github.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.junho.constant.DayOfWeek.DAY_OF_WEEK_COUNT;

// TODO: IssueGenerator에서 Map에 담은 issue를 돌면서 comment마다 빼서 markdown 만들기
public class MarkdownGenerator {
    public static final Map<DayOfWeek, GHIssue> currentWeekIssues = new EnumMap<>(DayOfWeek.class);

    public final List<Participant> participants = new ArrayList<>();

    private void setCurrentWeekIssues() {
        try {
            GitHub gitHub = GitHub.connect(PrivateInfo.ID, PrivateInfo.TOKEN);
            GHRepository repository = gitHub.getRepository("konkuk-tech-course/attendance-checker");


            // TODO: CLOSED 된 최근 7개만 가지고 온다.
            List<GHIssue> issues = repository.getIssues(GHIssueState.ALL).stream()
                    .limit(DAY_OF_WEEK_COUNT)
                    .collect(Collectors.toList());

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

            for (GHIssue issue : issues) {
                // TODO: 열린 날짜 계산 -> 월요일 부터 하나씩 가져오기.
                Date createdAt = issue.getCreatedAt();
                // TODO: Date에서 요일 추출하기. (분리 예정)
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(createdAt);
                String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.KOREAN);

                DayOfWeek matchedDay = DayOfWeek.findByDay(dayOfWeek);

                currentWeekIssues.put(matchedDay, issue);
            }
            //TODO: map에 잘 담겼는지 test
            currentWeekIssues.forEach((key, value) -> {
                try {
                    System.out.println(key + ": " + dateFormat.format(value.getCreatedAt()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setParticipants() {
        for (Map.Entry<DayOfWeek, GHIssue> entry : currentWeekIssues.entrySet()) {
            DayOfWeek day = entry.getKey();
            GHIssue gitHubIssue = entry.getValue();
            try {
                for (GHIssueComment comment : gitHubIssue.getComments()) {
                    final String userId = comment.getUser().getLogin();
                    final Participant member = new Participant(userId);

                    // TODO: 기존 존재 회원 출석체크
                    if (participants.contains(member)) {
                        Participant participant = findMember(member);
                        participant.checkAttendance(day);
                        continue;
                    }

                    // TODO: 새로운 회원 출석체크
                    participants.add(member);
                    member.checkAttendance(day);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // TODO: participants 이름 순으로 정렬
        participants.sort(Comparator.comparing(Participant::getUsername));
    }

    private Participant findMember(Participant member) {
        return participants.stream()
                .filter(i -> i.equals(member))
                .findAny()
                .orElseThrow();
    }

    private String createTable() {

        StringBuilder table = new StringBuilder(String.format("| 참여자 (%d) |", participants.size()));
        /*
         * | 참여자 (420) | 1주차 | 2주차 | 3주차 | 참석율 |
         * | --- | --- | --- | --- | --- |
         */

        List<String> dayOfWeeks = Arrays.stream(DayOfWeek.values())
                .map(DayOfWeek::toString)
                .collect(Collectors.toList());
        //TODO: 기본 테이블 형식 세팅
        for (String dayOfWeek : dayOfWeeks) {
            table.append(String.format(" %s |", dayOfWeek));
        }
        table.append(" 참석율 |\n");
        table.append("|:---:".repeat(dayOfWeeks.size() + 2));
        table.append("|\n");
        
        //TODO: participant마다 돌면서 이름과 참석 체크, 참석률 체크
        for (Participant participant : participants) {
            String username = participant.getUsername();
            EnumMap<DayOfWeek, Boolean> attendance = participant.getAttendance();

            table.append(String.format("| %s ", username));

            //TODO: 월, 화, 수, 목, 금, 토, 일 돌면서 체크 (일단위로 한다면 현재 날짜까지만 돌아야함)
            LocalDate now = LocalDate.now();
            String dayofWeekToday = now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
            int index = 0;
            for (DayOfWeek day : DayOfWeek.values()) {
                index++;
                //  TODO: 일단위로 한다면 현재 요일시 Break
                if (attendance.containsKey(day)) {
                    table.append("|:white_check_mark:");
                }
                else{
                    table.append("|:x:");
                }
                if (day.toString().equals(dayofWeekToday)){
                    break;
                }
            }
            //TODO: 현재 이후일은 표시 X
            table.append("| ".repeat(Math.max(0, DAY_OF_WEEK_COUNT - index)));
            table.append(String.format("| %.2f%% |\n", participant.getRate()));
        }
        return table.toString();
    }
    
    //TODO: 주차별 출석부 생성
    void createMarkdownFile(String table) throws IOException {
        final int weekOfYear = LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        String filepath = "docs/attendance-rate/";
        String fileName = weekOfYear + "-week-attendance-check.md";
        String pathAndName = filepath.concat(fileName);
        FileWriter fileWriter = new FileWriter(pathAndName);

        PrintWriter writer = new PrintWriter(fileWriter);
        writer.print("## :pushpin: "+weekOfYear+"주차 출석체크\n\n");
        writer.print(table);
        writer.close();

        //TODO: README를 최근 출석부로 변환
        updateReadMeWithCurrentAttendance(pathAndName);
    }

    private void updateReadMeWithCurrentAttendance(String pathName) throws IOException {
        FileReader fileReader = new FileReader(pathName);
        FileWriter fileWriter = new FileWriter("README.md");
        int data;
        while ((data = fileReader.read()) != -1) {
            fileWriter.write(data);
        }
        fileReader.close();
        fileWriter.close();
    }

    public static void main(String[] args) {
        MarkdownGenerator generator = new MarkdownGenerator();
        generator.setCurrentWeekIssues();
        generator.setParticipants();

        String table = generator.createTable();
        System.out.println(table);
        try {
            generator.createMarkdownFile(table);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}