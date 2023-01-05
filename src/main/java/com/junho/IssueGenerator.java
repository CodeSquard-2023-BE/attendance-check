package com.junho;

import com.junho.constant.PrivateInfo;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class IssueGenerator {

    public static final String LABEL = "documentation";

    public void createIssue() throws IOException{
        GHRepository repository = getRepositoryFromGitHub();
        final String title = generateTitle();
        final String content = generateIssueBody();
        repository.createIssue(title).body(content).label(LABEL).create();
    }

    private GHRepository getRepositoryFromGitHub() throws IOException {
        GitHub gitHub = GitHub.connect(PrivateInfo.ID, PrivateInfo.TOKEN);
        return gitHub.getRepository("konkuk-tech-course/attendance-checker");
    }

    private String generateTitle() {
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        int month = localDate.getMonth().getValue();
        int day = localDate.getDayOfMonth();
        String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
        return year + "년 " + month + "월 " + day + "일 " + dayOfWeek + " 스터디 쓰레드";
    }

    private String generateIssueBody() {
        String message = "위치: {0} \n{1}";
        String location = "스타벅스 군자사거리점";
        String notice = "출석 확인을 위해 comment를 달아주세요😊";
        return MessageFormat.format(message, location, notice);
    }

    public static void main(String[] args) {
        try {
            IssueGenerator issueGenerator = new IssueGenerator();
            issueGenerator.createIssue();
        } catch (IOException e) {
            throw new RuntimeException("Issue가 정상적으로 생성되지 않았습니다.",e);
        }
    }
}
