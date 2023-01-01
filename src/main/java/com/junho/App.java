package com.junho;

import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static final String ID = "juno-junho";
    public static final String TOKEN = "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";
    public static final Map<String, List<Integer>> memberAttendanceRecord = new HashMap<>();

    private String header(int totalNumberOfParticipants) {
        StringBuilder header = new StringBuilder(String.format("| 참여자 (%d) |", totalNumberOfParticipants));
        /*
         * | 참여자 (420) | 1주차 | 2주차 | 3주차 | 참석율 |
         * | --- | --- | --- | --- | --- |
         */
        List<String> weekDays = List.of("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일");
        System.out.println(weekDays);
        for (String weekDay : weekDays) {
            header.append(String.format(" %s |", weekDay));
        }
        header.append(" 참석율 |\n");
        header.append("| %s ".repeat(weekDays.size() + 2));
        header.append("|\n");
        return header.toString();
    }

    public static void main(String[] args) {
        App app = new App();

        try {
            GitHub gitHub = GitHub.connect(ID, TOKEN);
            GHRepository repository = gitHub.getRepository("konkuk-tech-course/attendance-checker");
//        GHRepository repository = gitHub.getRepository("konkuk-tech-course/attendance-checker");

            // organization member리스트
            GHOrganization organization = null;
            organization = gitHub.getOrganization("konkuk-tech-course");
            organization.listMembers().toList().stream().map(GHPerson::getLogin).forEach(i -> memberAttendanceRecord.put(i, new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        usernames= organization.listMembers().toList().stream().map(GHPerson::getLogin).collect(Collectors.toSet());
        int totalNumberOfMember = memberAttendanceRecord.size();
//        List<GHIssue> issues = repository.getIssues(GHIssueState.OPEN);
        // comment 돌면서 member 추가하기
        String header = app.header(totalNumberOfMember);
        System.out.println(header);
    }
}
