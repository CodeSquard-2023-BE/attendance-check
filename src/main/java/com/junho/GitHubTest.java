package com.junho;

import org.kohsuke.github.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class GitHubTest {

    public static Set<String> usernames = new HashSet<>();

    public static final String ID = "juno-junho";
    public static final String TOKEN = "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";
        public static LocalDate localDate = LocalDate.now();
//    public static LocalDate localDate = LocalDate.of(2023, 1, 1);
    public static void main(String[] args) {

        try {
            GitHub gitHub = GitHub.connect(ID, TOKEN);
            GHRepository repository = gitHub.getRepository("konkuk-tech-course/attendance-checker");
            List<GHIssue> issues = repository.getIssues(GHIssueState.OPEN);


            GHOrganization organization = gitHub.getOrganization("konkuk-tech-course");
            List<String> members = organization.listMembers().toList().stream().map(GHPerson::getLogin).collect(Collectors.toList());
            members.forEach(System.out::println);


            // 일단 있는거 클로스 해주기
            if (issues.size() != 1) {
                System.out.println("Open된 Issue가 여러개 있습니다");
            }
            GHIssue existingIssue = issues.get(0);
            // comment readme에 update
            existingIssue.close();

            int year = localDate.getYear();
            int month = localDate.getMonth().getValue();
            int day = localDate.getDayOfMonth();
            String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
            String title = year + "년 " + month + "월 " + day + "일 "+ dayOfWeek+  " 스터디 쓰레드";
            // 자동생성
            repository.createIssue(title).create();

            // 시간지나면 자동 closed
            List<GHIssue> openedIssue = repository.getIssues(GHIssueState.OPEN);
            // issue 크기가 1이 아니면 메세지

            if (openedIssue.size() != 1){
                System.out.println("Open된 Issue가 여러개 있습니다");
            }
            GHIssue currentIssue = issues.get(0);
            
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
                /*List<GHIssueComment> comments = issue.getComments();
                for (GHIssueComment comment : comments) {
                    System.out.println(comment.getUser().getLogin());

                }*/



    /*        GHOrganization organization = gitHub.getOrganization("konkuk-tech-course");
            List<String> members = organization.listMembers().toList().stream().map(GHPerson::getLogin).collect(Collectors.toList());
            members.forEach(System.out::println);

            organization.getRepositories();*/



        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        GHOrganization organization = gitHub.getOrganization()
    }
}
