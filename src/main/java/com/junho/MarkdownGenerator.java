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
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.junho.constant.DayOfWeek.DAY_OF_WEEK_COUNT;

// TODO: IssueGenerator에서 Map에 담은 issue를 돌면서 comment마다 빼서 markdown 만들기
public class MarkdownGenerator {
    public static final Map<DayOfWeek, GHIssue> currentWeekIssues = new EnumMap<>(DayOfWeek.class);

    public final List<Participant> participants = new ArrayList<>();

    private static final LocalDate now = LocalDate.now();

    private void setCurrentWeekIssues() {
        try {
            GHRepository repository = getRepositoryFromGitHub();
            List<GHIssue> issues = getCurrentSevenIssues(repository);
            issues.forEach(i -> {
                try {
                    System.out.println(i.getCreatedAt());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            setIssueWithMatchedDay(issues);

            //TODO: map에 잘 담겼는지 test
            printCurrentWeekIssues();
        } catch (IOException e) {
            throw new RuntimeException("issue를 담는데 예외가 발생했습니다.", e);
        }
    }

    private GHRepository getRepositoryFromGitHub() throws IOException {
        GitHub gitHub = GitHub.connect(PrivateInfo.ID, PrivateInfo.TOKEN);
        return gitHub.getRepository("konkuk-tech-course/attendance-checker");
    }

    private List<GHIssue> getCurrentSevenIssues(GHRepository repository) throws IOException {
        return repository.getIssues(GHIssueState.ALL).stream()
                .limit(DAY_OF_WEEK_COUNT)
                .collect(Collectors.toList());
    }

    private void setIssueWithMatchedDay(List<GHIssue> issues) throws IOException {
        for (GHIssue issue : issues) {
            DayOfWeek matchedDay = getCreatedDate(issue);
            System.out.println("matchedDay = " + matchedDay);
            currentWeekIssues.put(matchedDay, issue);
            // TODO: 2주차 부터 MONDAY로 변경 예정 (1월 9일 변경 예정)
            if (matchedDay == DayOfWeek.SUNDAY){
                break;
            }
        }
    }

    private DayOfWeek getCreatedDate(GHIssue issue) throws IOException {
        Date createdAt = issue.getCreatedAt();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createdAt);
        String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.KOREAN);
        return DayOfWeek.findByDay(dayOfWeek);
    }

    private void printCurrentWeekIssues() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일");
        currentWeekIssues.forEach((key, value) -> {
            try {
                System.out.println(key + ": " + dateFormat.format(value.getCreatedAt()));
            } catch (IOException e) {
                throw new RuntimeException("issue를 담는데 실패했습니다.", e);
            }
        });
    }

    private void setParticipants() throws IOException {
        System.out.println("currentWeekIssues = " + currentWeekIssues);
        for (Map.Entry<DayOfWeek, GHIssue> entry : currentWeekIssues.entrySet()) {
            final DayOfWeek day = entry.getKey();
            final GHIssue gitHubIssue = entry.getValue();
            System.out.println("day = " + day);
            setParticipantsWithComments(day, gitHubIssue);
        }
        // TODO: participants 이름 순으로 정렬
        sortParticipantsByUsername();
        participants.forEach( i -> System.out.println(i.getUsername() + i.getAttendance()));
    }

    private void setParticipantsWithComments(DayOfWeek day, GHIssue gitHubIssue) throws IOException {
        for (GHIssueComment comment : gitHubIssue.getComments()) {
            final String userId = comment.getUser().getLogin();
            final Participant member = new Participant(userId);

            // TODO: 기존 존재 회원 출석체크
            if (memberAlreadyExists(member)) {
                checkAttendance(day, member);
                continue;
            }
            // TODO: 새로운 회원 출석체크
            participants.add(member);
            member.checkAttendance(day);
        }
    }

    private boolean memberAlreadyExists(Participant member) {
        return participants.contains(member);
    }

    private void checkAttendance(DayOfWeek day, Participant member) {
        Participant participant = findMember(member);
        participant.checkAttendance(day);
    }

    private Participant findMember(Participant member) {
        return participants.stream()
                .filter(i -> i.equals(member))
                .findAny()
                .orElseThrow();
    }

    private void sortParticipantsByUsername() {
        participants.sort(Comparator.comparing(Participant::getUsername));
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
            System.out.println("username = " + username);
            System.out.println("attendance = " + attendance);

            table.append(String.format("| %s ", username));

            //TODO: 월, 화, 수, 목, 금, 토, 일 돌면서 체크 (일단위로 한다면 현재 날짜까지만 돌아야함)
            String dayofWeekToday = now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
            int index = 0;
            for (DayOfWeek day : DayOfWeek.values()) {
                index++;
                //  TODO: 일단위로 한다면 현재 요일시 Break
                if (attendance.containsKey(day)) {
                    table.append("|:white_check_mark:");
                } else {
                    table.append("|:x:");
                }
                if (day.toString().equals(dayofWeekToday)) {
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
        final int weekOfYear = now.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        String filepath = "docs/attendance-rate/";
        String fileName = weekOfYear + "-week-attendance-check.md";
        String pathAndName = filepath.concat(fileName);

        FileWriter fileWriter = new FileWriter(pathAndName);
        PrintWriter writer = new PrintWriter(fileWriter);
        writer.print(getTitle(weekOfYear));
        writer.print(table);
        writer.close();

        //TODO: README를 최근 출석부로 변환
        updateReadMeWithCurrentAttendance(pathAndName);
    }

    private String getTitle(int weekOfYear) {
        LocalDate firstMonday = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate thisSunday = firstMonday.plusDays(6);
        String monday = firstMonday.format(DateTimeFormatter.ofPattern("MM월 dd일"));
        String sunday = thisSunday.format(DateTimeFormatter.ofPattern("MM월 dd일"));

        String period = "(" + monday + " ~ " + sunday + ")";
        return "## :pushpin: " + weekOfYear + "주차 출석 현황 "+period+"\n\n";
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
        try {
            MarkdownGenerator generator = new MarkdownGenerator();
            generator.setCurrentWeekIssues();
            generator.setParticipants();

            String table = generator.createTable();
            System.out.println(table);

            generator.createMarkdownFile(table);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}