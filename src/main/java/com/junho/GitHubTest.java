package com.junho;

import org.kohsuke.github.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class GitHubTest {

    public static Set<String> usernames = new HashSet<>();

    public static final String ID = "juno-junho";
    public static final String TOKEN = "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";
//    static LocalDate localDate = LocalDate.now();
        public static LocalDate localDate = LocalDate.of(2023, 1, 2);

    public static final Map<String, List<Integer>> memberAttendanceRecord = new HashMap<>();
    private static String generateTitle() {
        int year = localDate.getYear();
        int month = localDate.getMonth().getValue();
        int day = localDate.getDayOfMonth();
        String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
        return year + "년 " + month + "월 " + day + "일 " + dayOfWeek + " 스터디 쓰레드";
    }

    public static void main(String[] args) {

        try {
            // github token으로 연결
            GitHub gitHub = GitHub.connect(ID, TOKEN);
            GHRepository repository = gitHub.getRepository("konkuk-tech-course/attendance-checker");

            // organization member리스트
            GHOrganization organization = gitHub.getOrganization("konkuk-tech-course");
            Set<String> members = organization.listMembers().toList().stream().map(GHPerson::getLogin).collect(Collectors.toSet());
            int totalNumberOfMember = members.size();
//            members.forEach(System.out::println);

            // open된것에서 comment 가져와 readme 만들기
            List<GHIssue> issues = repository.getIssues(GHIssueState.OPEN);

            // 2. 00시 되면 존재하는 issue 클로스 해주기
            if (issues.size() != 1) {
                System.out.println("Open된 Issue가 여러개 있습니다");
            }
            GHIssue existingIssue = issues.get(0);
            existingIssue.close();

            // 3. closed 된 후 그날 opened된 issue 생성
            String title = generateTitle();
            repository.createIssue(title).create();
            List<GHIssue> openedIssue = repository.getIssues(GHIssueState.OPEN);
            if (openedIssue.size() != 1) {
                System.out.println("Open된 Issue가 여러개 있습니다");
            }
            GHIssue currentIssue = issues.get(0);

            // readme 만들기


            // 이건 나중에 돌면서 comment로 출석체그
            currentIssue.getComments().forEach(comment -> {
                try {
                    usernames.add(comment.getUser().getLogin());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            usernames.forEach(System.out::println);

//            repository.createIssue(title).create();
//            for (GHIssue issue : issues) {
//                // 쓰레드 자동생성 기능 만들기.
//
//                Date closedAt = issue.getClosedAt();
//                long time = closedAt.getTime();
//                System.out.println(time);
//                issue.getComments();
//                String member = issue.getUser().getLogin();
//                System.out.println(member);
                List<GHIssueComment> comments = currentIssue.getComments();
                for (GHIssueComment comment : comments) {
                    System.out.println(comment.getUser().getLogin());
                }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        GHOrganization organization = gitHub.getOrganization()
    }

   /* private String header(int totalNumberOfParticipants) {
        StringBuilder header = new StringBuilder(String.format("| 참여자 (%d) |", totalNumberOfParticipants));
        *//**
         * | 참여자 (420) | 1주차 | 2주차 | 3주차 | 참석율 |
         * | --- | --- | --- | --- | --- |
         *//*
        List<String> weekDays = Arrays.stream(DayOfWeek.values())
                .map(i -> i.getDisplayName(TextStyle.FULL, Locale.KOREAN))
                .collect(Collectors.toList());

        System.out.println(weekDays);

        for (String weekDay : weekDays) {
            header.append(String.format(" %s |", weekDay));
        }
        header.append(" 참석율 |\n");


        header.append("| %s ".repeat(weekDays.size() + 2));

        header.append("|\n");

        return header.toString();
    }

    public static void main(String[] args) throws IOException {
        GitHubTest gitHubTest = new GitHubTest();

        GitHub gitHub = GitHub.connect(ID, TOKEN);
        GHRepository repository = gitHub.getRepository("konkuk-tech-course/attendance-checker");

        // organization member리스트
        GHOrganization organization = gitHub.getOrganization("konkuk-tech-course");
        organization.listMembers().toList().stream().map(GHPerson::getLogin).forEach(i -> memberAttendanceRecord.put(i, new ArrayList<>()));
//        usernames= organization.listMembers().toList().stream().map(GHPerson::getLogin).collect(Collectors.toSet());
        int totalNumberOfMember = memberAttendanceRecord.size();

        List<GHIssue> issues = repository.getIssues(GHIssueState.OPEN);



        // comment 돌면서 member 추가하기
        String header = gitHubTest.header(totalNumberOfMember);
        System.out.println(header);




    }*/
}
