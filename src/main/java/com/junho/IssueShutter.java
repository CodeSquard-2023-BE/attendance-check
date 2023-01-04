package com.junho;

import com.junho.constant.PrivateInfo;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.List;

// TODO: ISSUE를 닫는 기능을 한다. -> 언제쓰지..
public class IssueShutter {

    public void closeIssue() throws IOException {
        GHRepository repository = getRepositoryFromGitHub();

        // TODO: 열려있는 오늘의 ISSUE CLOSE. OPENED ISSUE가 하나 이상이면 EXCEPTION 발생
        List<GHIssue> openedIssue = repository.getIssues(GHIssueState.OPEN);

        // TODO: 하나 이상의 OPENED 된 ISSUE가 있는지 VALIDATE
        assert checkIfMoreThanOneIssuesExist(openedIssue) : "하나 이상의 opened된 issue가 있습니다";

        GHIssue issuesCreatedToday = openedIssue.get(0);
        issuesCreatedToday.close();
        System.out.println("성공적으로 closed 되었습니다.");
    }

    private GHRepository getRepositoryFromGitHub() throws IOException {
        GitHub gitHub = GitHub.connect(PrivateInfo.ID, PrivateInfo.TOKEN);
        return gitHub.getRepository("konkuk-tech-course/attendance-checker");
    }

    private boolean checkIfMoreThanOneIssuesExist(List<GHIssue> openedIssue) {
        return openedIssue.size() != 1;
    }

    public static void main(String[] args) {
        try {
            IssueShutter shutter = new IssueShutter();
            shutter.closeIssue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
