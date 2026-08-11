import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class GitHub_Informer_New {
	public static void main(String args[]) {
		System.out.println("Calling Cliq...");
		Integer MAX_MESSAGE_LENGTH = 4096;
		String MESSAGE_BREAK = "\\n";
		Integer status = 400;
		boolean MESSAGE_SEND_FAILURE_ERROR = true;
		boolean INVALID_ENDPOINT_ERROR = true;
		boolean GITHUB_ERROR = true;
		String ERROR_MESSAGE = new String("Multiple Errors Occured");
		StringBuffer responseContent = new StringBuffer();
		try {
			String message;
			String CustomMessage;
			String ServerURL = "https://www.github.com/";
			if(args == null || args.length == 0 || args[0] == null || args[0].isBlank())
			{
				ERROR_MESSAGE = "Invalid Endpoint. Input 'channel-endpoint' is missing or empty.";
				return;
			}
			String CliqChannelLink = args[0];
			if(CliqChannelLink.contains("message") && CliqChannelLink.contains("https://cliq.zoho") && CliqChannelLink.contains("/api/v2/") && CliqChannelLink.contains("?zapikey="))
			  INVALID_ENDPOINT_ERROR = false;
			CustomMessage = (String) System.getenv("CUSTOM_MESSAGE");
			String Actor = (String) System.getenv("GITHUB_ACTOR");
			String ActorURL = ServerURL + Actor;
			String Event = (String) System.getenv("GITHUB_EVENT_NAME");
			String[] EventWords = Event.split("_");
			String Repository = (String) System.getenv("GITHUB_REPOSITORY");
			String RepositoryURL = ServerURL + Repository;
			Event = new String();
			for(String s: EventWords)
			  Event += s.substring(0,1).toUpperCase() + s.substring(1) + " ";
			Event = Event.trim();
			String ActionRaw = (String) System.getenv("ACTION");
			String Action = ActionRaw;
			if(Action != null && !Action.isBlank())
			{
			  String[] ActionWords = Action.split("_");
			  Action = new String();
			  for(String s: ActionWords)
			    Action += s + " ";
			  Action = Action.trim();
			}
			else
			{
				Action = "made";
			}
			String GitHubInformerURL = "https://workdrive.zohoexternal.com/external/a55ce4b1d1b64d36de31b77b6067d0a74b47b8733459390605c849bc880b05e8/download?directDownload=true";
			message = CustomMessage;
			if(CustomMessage != null)
			{
				if(CustomMessage.equals("_+_"))
				{
					message = new String();
					if(Event.equals("Branch Protection Rule"))
					{
						String Branch_Manager = (String) System.getenv("GITHUB_ACTOR");
						String Rule = (String) System.getenv("BRANCH_RULE");
						String RuleID = (String) System.getenv("BRANCH_RULE_ID");
						if(Action.equals("created"))
						{
							message = "[" + Branch_Manager + "](" + ServerURL + Branch_Manager + ") has created a new branch protection rule - [" + Rule + "](" + RepositoryURL + "/settings/branch_protection_rules/" + RuleID + ")";
						}
						else if(Action.equals("deleted"))
						{
							message = "[" + Branch_Manager + "](" + ServerURL + Branch_Manager + ") has deleted an existing branch protection rule - " + Rule;
						}
						else if(Action.equals("edited"))
						{
							message = "[" + Branch_Manager + "](" + ServerURL + Branch_Manager + ") has edited an existing branch protection rule - [" + Rule + "](" + RepositoryURL + "/settings/branch_protection_rules/" + RuleID + ")";
						}
					}
					else if(Event.equals("Check Run"))
					{
						String Checker = (String) System.getenv("GITHUB_ACTOR");
						String CheckName = (String) System.getenv("CHECK_RUN_NAME");
						String ChecksURL = (String) System.getenv("CHECK_RUN_URL");
						if(Action.equals("created"))
						{
							message = "[" + Checker + "](" + ServerURL + Checker + ") has created a new check run - [" + CheckName + "](" + ChecksURL + ")";
						}
						else if(Action.equals("completed"))
						{
							message = "The check run [" + CheckName + "](" + ChecksURL + ") created by [" + Checker + "](" + ServerURL + Checker + ") has been completed";
						}
					}
					else if(Event.equals("Check Suite"))
					{
						String CheckSuiter = (String) System.getenv("GITHUB_ACTOR");
						message = "The check suite created by [" + CheckSuiter + "](" + ServerURL + CheckSuiter + ") has been completed";
					}
					else if(Event.equals("Create"))
					{
						String Creator = (String) System.getenv("GITHUB_ACTOR");
						String Ref = (String) System.getenv("BRANCH_NAME");
						String RefType = (String) System.getenv("BRANCH_TYPE");
	 					message = "[" + Creator + "](" + ServerURL + Creator + ") has created a new " + RefType + " - [" + Ref + "](" + ServerURL + Repository + "/tree/" + Ref + ")";
					}
					else if(Event.equals("Delete"))
					{
						String Deletor = (String) System.getenv("GITHUB_ACTOR");
						String Ref = (String) System.getenv("BRANCH_NAME");
						String RefType = (String) System.getenv("BRANCH_TYPE");
						message = "[" + Deletor + "](" + ServerURL + Deletor + ") has deleted the " + RefType + " - " + Ref;
					}
					else if(Event.equals("Deployment"))
					{
						String Deployer = (String) System.getenv("GITHUB_ACTOR");
						String DeploymentEnv = (String) System.getenv("DEPLOYMENT_ENV");
						String DeploymentURL = (String) System.getenv("DEPLOYMENT_URL");
					    DeploymentURL = DeploymentURL.replace("api","www");
					    DeploymentURL = DeploymentURL.replace("/repos","");
						message = "A new deployment - " + DeploymentEnv + " - has been created for the repository - [" + Repository + "](" + RepositoryURL + ")";
					}
					else if(Event.equals("Deployment Status"))
					{
						String Deployer = (String) System.getenv("GITHUB_ACTOR");
						String DeploymentEnv = (String) System.getenv("DEPLOYMENT_ENV");
						String DeploymentURL = (String) System.getenv("DEPLOYMENT_URL");
					    DeploymentURL = DeploymentURL.replace("api","www");
					    DeploymentURL = DeploymentURL.replace("/repos","");
						String Status = (String) System.getenv("STATUS");
						Status = Status.replace("_"," ");
						message = "The status of the deployment [" + DeploymentEnv + "](" + DeploymentURL + ") associated with the [" + Repository + "](" + RepositoryURL + ") repository has been changed to " + Status;
					}
					else if(Event.equals("Discussion"))
					{
						String Discusser = (String) System.getenv("GITHUB_ACTOR");
						String Discussion = (String) System.getenv("DISCUSSION");
						String DiscussionURL = (String) System.getenv("DISCUSSION_URL");
						if(Action.equals("created"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has created a new discussion - [" + Discussion + "](" + DiscussionURL + ")";
						}
						else if(Action.equals("deleted"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has deleted the discussion - [" + Discussion + "](" + DiscussionURL + ")"; 
						}
						else if(Action.equals("edited"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has edited the discussion - [" + Discussion + "](" + DiscussionURL + ")";
						}
						else if(Action.equals("pinned"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has pinned the discussion - [" + Discussion + "](" + DiscussionURL + ")";
						}
						else if(Action.equals("unpinned"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has unpinned the discussion - [" + Discussion + "](" + DiscussionURL + ")";
						}
						else if(Action.equals("labeled"))
						{
							String LabelName = (String) System.getenv("LABEL_NAME");
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has labeled the discussion [" + Discussion + "](" + DiscussionURL + ") as [" + LabelName + "](" + RepositoryURL+ "/discussions?discussions_q=label%3A" + LabelName + ")";
						}
						else if(Action.equals("unlabeled"))
						{
							String LabelName = (String) System.getenv("LABEL_NAME");
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has removed the discussion [" + Discussion + "](" + DiscussionURL + ") from the label [" + LabelName + "](" + RepositoryURL+ "/discussions?discussions_q=label%3A" + LabelName + ")";
						}
						else if(Action.equals("locked"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has locked the discussion - [" + Discussion + "](" + DiscussionURL + ")";
						}
						else if(Action.equals("unlocked"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has unlocked the discussion - [" + Discussion + "](" + DiscussionURL + ")";
						}
						else if(Action.equals("transferred"))
						{
							String NewRepository = (String) System.getenv("NEW_REPOSITORY");
							String NewRepositoryURL = ServerURL + NewRepository;
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has transferred the discussion [" + Discussion + "](" + DiscussionURL + ") from [" + Repository + "](" + RepositoryURL + ") to [" + NewRepository + "](" + NewRepositoryURL + ")";
						}
						else if(Action.equals("answered"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has added an answer to the discussion - [" + Discussion + "](" + DiscussionURL + ")";
						}
						else if(Action.equals("unanswered"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has unmarked an answer from the discussion - [" + Discussion + "](" + DiscussionURL + ")";
						}
						else if(Action.equals("category changed"))
						{
							String CategoryName = (String) System.getenv("CATEGORY_NAME");
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has changed and added the discussion [" + Discussion + "](" + DiscussionURL + ") under the [" + CategoryName + "](" + RepositoryURL + "/discussions/categories/" + CategoryName + ") category";
						}
					}
					else if(Event.equals("Discussion Comment"))
					{
						String Discusser = (String) System.getenv("GITHUB_ACTOR");
						String DiscussionTitle = (String) System.getenv("DISCUSSION");
						String DiscussionComment = (String) System.getenv("DISCUSSION_COMMENT");
						String DiscussionURL = (String) System.getenv("DISCUSSION_URL");
						String CommentURL = (String) System.getenv("COMMENT_URL");
						if(Action.equals("created"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has added a new [comment](" + CommentURL + ") to the discussion - [" + DiscussionTitle + "](" + DiscussionURL + ")";
						}
						else if(Action.equals("edited"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has edited a [comment](" + CommentURL + ") attached to the discussion - [" + DiscussionTitle + "](" + DiscussionURL + ")";
						}
						else if(Action.equals("deleted"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has deleted a [comment](" + CommentURL + ") attached with the discussion - [" + DiscussionTitle + "](" + DiscussionURL + ")";
						}
					}
					else if(Event.equals("Fork"))
					{
						String Forker = (String) System.getenv("GITHUB_ACTOR");
						String Forkee = (String) System.getenv("NEW_REPOSITORY");
						String RepoOwner = (String) System.getenv("GITHUB_REPOSITORY_OWNER");
						String ForkerURL = ServerURL + Forker;
						String RepoOwnerURL = ServerURL + RepoOwner;
						String ForkeeURL = ServerURL + Forkee;
						message = "[" + Forker + "](" + ForkerURL + ") has forked [" + RepoOwner + "](" + RepoOwnerURL + ") 's [" + Repository + "](" + RepositoryURL + ") repository to [" + Actor + "](" + ActorURL + ") 's [" + Forkee + "](" + ForkeeURL + ") repository";
					}
					else if(Event.equals("Gollum"))
					{
						String PageHandler = (String) System.getenv("GITHUB_ACTOR");
						String Pages = (String) System.getenv("PAGES");
						ArrayList<HashMap<String,String>> PageArray = new ArrayList<HashMap<String,String>>();
						HashMap<String,String> Page = new HashMap<String,String>();
						for (String Line: Pages.split("\n"))
						{
						    if(Line.contains("title") || Line.contains("html_url") || Line.contains("action"))
						    {
								String[] keyValuePair= LineBreaker(Line);
								Page.put(keyValuePair[0],keyValuePair[1]);
						    }
						    if(Line.contains("}"))
						    {
								PageArray.add(Page);
                				Page = new HashMap<String,String>();
						    }
						}
						if(PageArray.size() > 1)
						{
							message = "A few changes has been made to the [Wiki pages](" + RepositoryURL + "/wiki) of [" + Repository + "](" + RepositoryURL + ") by [" + PageHandler + "](" + ServerURL + PageHandler + ")";
							message = message + "\\nHere is a list of the Changes\\n";
						}
						for (HashMap<String,String> PageDetails : PageArray)
						{
						    if(PageDetails.get("title").toLowerCase().contains("_footer"))
							message = message + "\\n:task: The [Footer](" + PageDetails.get("html_url") + ") has been " + PageDetails.get("action");
						    else if(PageDetails.get("title").toLowerCase().contains("_sidebar"))
							message = message + "\\n:task: The [Sidebar](" + PageDetails.get("html_url") + ") has been " + PageDetails.get("action");
						    else
							message = message + "\\n:task: The Page [" + PageDetails.get("title") + "](" + PageDetails.get("html_url") + ") has been " + PageDetails.get("action") ;
						}
						if(PageArray.size() == 1)
						{
							message = message + " at [" + Repository + "](" + RepositoryURL + ") by [" + PageHandler + "](" + ServerURL + PageHandler + ")";
						}
					}
					else if(Event.equals("Issues"))
					{
						String Issuer= (String) System.getenv("GITHUB_ACTOR");
						String IssueName = (String) System.getenv("ISSUE_TITLE");
						IssueName = IssueName + " #" + System.getenv("ISSUE_NUMBER");
						String IssueURL = System.getenv("ISSUE_URL");
						if(Action.equals("opened"))
						{
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has created a new issue - [" + IssueName + "](" + IssueURL + ")";
						}
						else if(Action.equals("closed"))
						{
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has closed the issue - [" + IssueName + "](" + IssueURL + ")";
						}
						else if(Action.equals("edited"))
						{
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has edited the issue - [" + IssueName + "](" + IssueURL + ")";
						}
						else if(Action.equals("reopened"))
						{
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has reopened the issue - [" + IssueName + "](" + IssueURL + ")";
						}
						else if(Action.equals("deleted"))
						{
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has deleted the issue - [" + IssueName + "](" + IssueURL + ")";
						}
						else if(Action.equals("transferred"))
						{
							String NewRepository = (String) System.getenv("NEW_REPOSITORY");
							String NewRepositoryURL = ServerURL + NewRepository;
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has transferred the issue [" + IssueName + "](" + IssueURL + ") from [" + Repository + "](" + RepositoryURL + ") to [" + NewRepository + "](" + NewRepositoryURL + ")";
						}
						else if(Action.equals("assigned"))
						{
							String AssignedUser = (String) System.getenv("ASSIGNED_USER");
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has assigned the issue [" + IssueName + "](" + IssueURL + ") to [" + AssignedUser + "](" + ServerURL + AssignedUser + ")";
						}
						else if(Action.equals("unassigned"))
						{
							String AssignedUser = (String) System.getenv("ASSIGNED_USER");
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has unassigned the issue [" + IssueName + "](" + IssueURL + ") from [" + AssignedUser + "](" + ServerURL + AssignedUser + ")";	
						}
						else if(Action.equals("labeled"))
						{
							String LabelName = (String) System.getenv("ASSIGNED_LABEL");
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has labelled the issue [" + IssueName + "](" + IssueURL + ") as " + LabelName;
						}
						else if(Action.equals("unlabeled"))
						{
							String LabelName = (String) System.getenv("ASSIGNED_LABEL");
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has removed the issue [" + IssueName + "](" + IssueURL + ") from the label " + LabelName;
						}
						else if(Action.equals("locked"))
						{
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has locked the issue - [" + IssueName + "](" + IssueURL + ")";
						}
						else if(Action.equals("unlocked"))
						{
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has unlocked the issue - [" + IssueName + "](" + IssueURL + ")";
						}
						else if(Action.equals("pinned"))
						{
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has pinned the issue - [" + IssueName + "](" + IssueURL + ")";
						}
						else if(Action.equals("unpinned"))
						{
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has unpinned the issue - [" + IssueName + "](" + IssueURL + ")";
						}
						else if(Action.equals("milestoned"))
						{
							String Milestone = (String) System.getenv("MILESTONE");
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") has set a milestone for the issue - [" + IssueName + "](" + IssueURL + ") with " + Milestone;
						}
						else if(Action.equals("demilestoned"))
						{
							message = "[" + Issuer + "](" + ServerURL + Issuer + ") removed the milestone that was set for the issue - [" + IssueName + "](" + IssueURL + ")";
						}
					}
					else if(Event.equals("Issue Comment"))
					{
						String Issuer = (String) System.getenv("GITHUB_ACTOR");
						String IssueType = (String) System.getenv("ISSUE_TYPE");
						String IssueName = (String) System.getenv("ISSUE_TITLE");
						IssueName = IssueName + " #" +  (String) System.getenv("ISSUE_NUMBER");
						String IssueURL = (String) System.getenv("ISSUE_URL");
						String IssueComment = (String) System.getenv("ISSUE_COMMENT");
						if(IssueType.equals("ISSUE"))
						{
							if(Action.equals("created"))
							{
								message = "[" + Issuer + "](" + ServerURL + Issuer + ") has added a new comment to the issue - [" + IssueName + "](" + IssueURL + ")";
							}
							else if (Action.equals("deleted")) 
							{
								message = "[" + Issuer + "](" + ServerURL + Issuer + ") has deleted a comment to the issue - [" + IssueName + "](" + IssueURL + ")";
							}
							else if(Action.equals("edited"))
							{
								message = "[" + Issuer + "](" + ServerURL + Issuer + ") has edited a comment made to the issue - [" + IssueName + "](" + IssueURL + ")";
							}
						}
						else if(IssueType.equals("PULL_REQUEST"))
						{
							if(Action.equals("created"))
							{
								message = "[" + Issuer + "](" + ServerURL + Issuer + ") has added a new comment to the pull request [" + IssueName + "](" + IssueURL + ")";
							}
							else if (Action.equals("deleted")) 
							{
								message = "[" + Issuer + "](" + ServerURL + Issuer + ") has deleted a new comment to the pull request [" + IssueName + "](" + IssueURL + ")";
							}
							else if(Action.equals("edited"))
							{
								message = "[" + Issuer + "](" + ServerURL + Issuer + ") has edited a comment made to the pull request- [" + IssueName + "](" + IssueURL + ")";
							}
						}
					}
					else if(Event.equals("Label"))
					{
						String Labeler = (String) System.getenv("GITHUB_ACTOR");
						String LabelName = (String) System.getenv("LABEL_NAME");
						String NewWord = new String();
						if(Action.equals("created"))
							NewWord = "new ";
						message = "[" + Labeler + "](" + ServerURL + Labeler + ") has " + Action + " a " + NewWord + "label - " + LabelName;
					}
					else if(Event.equals("Milestone"))
					{
						String Milestoner = (String) System.getenv("GITHUB_ACTOR");
						String MilestoneName = (String) System.getenv("MILESTONE");
						String MilestoneURL = (String) System.getenv("MILESTONE_URL");
						String NewWord = new String();
						if(Action.equals("created"))
							NewWord = "new ";
						else if(Action.equals("opened"))
							Action =  "reopened";
						else if(Action.equals("deleted"))
							MilestoneURL = RepositoryURL + "/milestones";
						message = "[" + Milestoner + "](" + ServerURL + Milestoner + ") has " + Action + " a " + NewWord + "milestone - [" + MilestoneName + "](" + MilestoneURL +")";
					}
					else if(Event.equals("Page Build"))
					{
						String PageBuilder = (String) System.getenv("GITHUB_ACTOR");
						message = "A new page build has been created for the repository - [" + Repository + "](" + RepositoryURL + ") by " + "[" + PageBuilder + "](" + ServerURL + PageBuilder + ")";
					}
					else if(Event.equals("Public"))
					{
						String Publicizer = (String) System.getenv("GITHUB_ACTOR");
						message = "The [" + Repository + "](" + RepositoryURL + ") repository has been made public by [" + Publicizer + "](" + ServerURL + Publicizer + ")";
					}
					else if(Event.equals("Pull Request") || Event.equals("Pull Request Target"))
					{
						String PullRequestOperator = (String) System.getenv("GITHUB_ACTOR");
						String PullRequest = (String) System.getenv("PULL_REQUEST_TITLE");
						PullRequest = PullRequest + " #" + (String) System.getenv("PULL_REQUEST_NUMBER");
						String PullRequestURL = (String) System.getenv("PULL_REQUEST_URL");

						if(Action.equals("opened"))
						{
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has opened a new " + Event + " [" + PullRequest + "](" + PullRequestURL + ") for the repository [" + Repository + "](" + RepositoryURL + ")";
						}
						else if(Action.equals("edited"))
						{
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has edited the " + Event + " [" + PullRequest + "](" + PullRequestURL + ") attached with the repository [" + Repository + "](" + RepositoryURL + ")";
						}
						else if(Action.equals("reopened"))
						{
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has reopened the " + Event + " [" + PullRequest + "](" + PullRequestURL + ") for the repository [" + Repository + "](" + RepositoryURL + ")";
						}
						else if(Action.equals("assigned"))
						{
							String AssignedUser = (String) System.getenv("ASSIGNED_USER");
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has assigned the " + Event + " [" + PullRequest + "](" + PullRequestURL + ") to [" + AssignedUser + "](" + ServerURL + AssignedUser + ")";
						}
						else if(Action.equals("unassigned"))
						{
							String AssignedUser = (String) System.getenv("ASSIGNED_USER");
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has unassigned the " + Event + " [" + PullRequest + "](" + PullRequestURL + ") from [" + AssignedUser + "](" + ServerURL + AssignedUser + ")";
						}
						else if(Action.equals("labeled"))
						{
							String LabelName = (String) System.getenv("ASSIGNED_LABEL");
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has labelled the " + Event + " [" + PullRequest + "](" + PullRequestURL + ") as " + LabelName;
						}
						else if(Action.equals("unlabeled"))
						{
							String LabelName = (String) System.getenv("ASSIGNED_LABEL");
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has removed the " + Event + " [" + PullRequest + "](" + PullRequestURL + ") from the label " + LabelName;
						}
						else if(Action.equals("locked"))
						{
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has locked the " + Event + " - [" + PullRequest + "](" + PullRequestURL + ")";
						}
						else if(Action.equals("unlocked"))
						{
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has unlocked the " + Event + " - [" + PullRequest + "](" + PullRequestURL + ")";
						}
						else if(Action.equals("converted to draft"))
						{
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has marked the " + Event + " [" + PullRequest + "](" + PullRequestURL + ") as draft";
						}
						else if(Action.equals("ready for review"))
						{
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has marked the " + Event + " [" + PullRequest + "](" + PullRequestURL + ") as ready for review";
						}
						else if(Action.equals("review requested"))
						{
							String AssignedUser = (String) System.getenv("ASSIGNED_USER");
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has requested a review for [" + PullRequest + "](" + PullRequestURL + ") [" + AssignedUser + "](" + ServerURL + AssignedUser + ")";
						}
						else if(Action.equals("review request removed"))
						{
							String AssignedUser = (String) System.getenv("ASSIGNED_USER");
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has removed that review request for [" + PullRequest + "](" + PullRequestURL + ") assigned to [" + AssignedUser + "](" + ServerURL + AssignedUser + ")";
						}
						else if(Action.equals("auto merge enabled"))
						{
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has enabled the auto merge option";
						}
						else if(Action.equals("auto merge disabled"))
						{
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has disabled the auto merge option";
						}
						else if(Action.equals("synchronize"))
						{
							message = "New changes have been added to the " + Event + " - [" + PullRequest + "](" + PullRequestURL + ")";
						}
						else if(Action.equals("closed"))
						{
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has closed the pull request [" + PullRequest + "](" + PullRequestURL + ")";
						}
						else if(Action.equals("milestoned"))
						{
							String Milestone = (String) System.getenv("MILESTONE");
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has milestoned the pull request [" + PullRequest + "](" + PullRequestURL + ") with " + Milestone;
						}
						else if(Action.equals("demilestoned"))
						{
							message = "[" + PullRequestOperator + "](" + ServerURL + PullRequestOperator + ") has demilestoned the pull request [" + PullRequest + "](" + PullRequestURL + ")";
						}
					}
					else if(Event.equals("Pull Request Review"))
					{
						String Reviewer = (String) System.getenv("GITHUB_ACTOR");
						String PullRequest = (String) System.getenv("PULL_REQUEST_TITLE");
						PullRequest = PullRequest + " " + (String) System.getenv("PULL_REQUEST_NUMBER");
						String PullRequestURL = (String) System.getenv("PULL_REQUEST_URL");
						String PullRequestReviewURL = (String) System.getenv("PULL_REQUEST_REVIEW_URL");
						if(Action.equals("submitted"))
						{
							message = "[" + Reviewer + "](" + ServerURL + Reviewer + ") has submitted a [review](" + PullRequestReviewURL + ") for the pull request [" + PullRequest + "](" + PullRequestURL + ")";
						}
						else if(Action.equals("dismissed"))
						{
							message = "[" + Reviewer + "](" + ServerURL + Reviewer + ") has dismissed a [review](" + PullRequestReviewURL + ") for the pull request [" + PullRequest + "](" + PullRequestURL + ")";
						}
						else if(Action.equals("edited"))
						{
							message = "[" + Reviewer + "](" + ServerURL + Reviewer + ") has edited the [review details](" + PullRequestReviewURL + ") for the pull request [" + PullRequest + "](" + PullRequestURL + ")";
						}
					}
					else if(Event.equals("Pull Request Review Comment"))
					{
						String Commentor = (String) System.getenv("GITHUB_ACTOR");
						String PullRequest = (String) System.getenv("PULL_REQUEST_TITLE");
						PullRequest = PullRequest + " " + (String) System.getenv("PULL_REQUEST_NUMBER");
						String PullRequestURL = (String) System.getenv("PULL_REQUEST_URL");
						if(Action.equals("created"))
							message = "[" + Commentor + "](" + ServerURL + Commentor + ") has created a new [pull request review comment](" + PullRequestURL + ")";
						else if(Action.equals("edited"))
							message = "[" + Commentor + "](" + ServerURL + Commentor + ") has edited a [pull request review comment](" + PullRequestURL + ")";
						else if(Action.equals("deleted"))
							message = "[" + Commentor + "](" + ServerURL + Commentor + ") has deleted a [pull request review comment](" + PullRequestURL + ")";
					}	
					else if(Event.equals("Push"))
					{
						String Pusher = (String) System.getenv("GITHUB_ACTOR");
						String Branch_Name = (String) System.getenv("GITHUB_REF_NAME");
						String Branch_Type = (String) System.getenv("GITHUB_REF_TYPE");
						String Commit_URL = (String) System.getenv("COMMIT_URL");
						String Compare_URL = (String) System.getenv("COMPARE_URL");
						message ="[" + Pusher + "](" + ServerURL + Pusher + ") has pushed a new [code](" + Commit_URL + ") in the " + Branch_Type + " [" + Branch_Name + "](" + ServerURL + Repository + "/tree/" + Branch_Name + ")";
					}
					else if(Event.equals("Registry Package"))
					{
						String Publisher = (String) System.getenv("GITHUB_ACTOR");
						String RegistryPackageName = (String) System.getenv("REGISTRY_PACKAGE_NAME");
						String RegistryPackageVersion = (String) System.getenv("REGISTRY_PACKAGE_VERSION");
						String RegistryPackageType = (String) System.getenv("REGISTRY_PACKAGE_TYPE");
						String RegistryPackageURL = (String) System.getenv("REGISTRY_PACKAGE_URL");
						if(Action.equals("published"))
						{
							message = "[" + Publisher + "](" + ServerURL + Publisher + ") has published a new " + RegistryPackageType + " registry package [" + RegistryPackageName + " " + RegistryPackageVersion + "](" + RegistryPackageURL + ")";
						}
					}
					else if(Event.equals("Release"))
					{
						String Releaser = (String) System.getenv("GITHUB_ACTOR");
						String ReleaseName = (String) System.getenv("RELEASE_NAME");
						String ReleaseTagName = (String) System.getenv("RELEASE_TAG");
						String ReleaseURL = (String) System.getenv("RELEASE_URL");
						if(Action.equals("published"))
						{
							message = "[" + Releaser + "](" + ServerURL + Releaser + ") has published a new release - [" + ReleaseName + " " + ReleaseTagName + "](" + ReleaseURL + ")";
						}
						else if(Action.equals("created"))
						{
							message = "[" + Releaser + "](" + ServerURL + Releaser + ") has created a new release - [" + ReleaseName + " " + ReleaseTagName + "](" + ReleaseURL + ")";
						}
						else if(Action.equals("prereleased"))
						{
							message = "[" + Releaser + "](" + ServerURL + Releaser + ") has moved [" + ReleaseName + " " + ReleaseTagName + "](" + ReleaseURL + ") to the prerelease stage";
						}
						else if(Action.equals("released"))
						{
							message = "[" + Releaser + "](" + ServerURL + Releaser + ") has released [" + ReleaseName + " " + ReleaseTagName + "](" + ReleaseURL + ")";
						}
						else if(Action.equals("edited"))
						{
							message = "[" + Releaser + "](" + ServerURL + Releaser + ") has edited and made changes to the release [" + ReleaseName + " " + ReleaseTagName + "](" + ReleaseURL + ")";
						}
						else if(Action.equals("deleted"))
						{
							message = "[" + Releaser + "](" + ServerURL + Releaser + ") has deleted a release " + ReleaseName + " " + ReleaseTagName ;
						}
					}
					else if(Event.equals("Repository Dispatch"))
					{
						String Trigger_Actor = (String) System.getenv("GITHUB_ACTOR");
						String WorkflowID = (String) System.getenv("GITHUB_WORKFLOW");
						String WorkflowURL = ServerURL + Repository + "/actions/runs/" + WorkflowID;
						message = "[" + Trigger_Actor + "](" + ServerURL + Trigger_Actor + ") has triggered a new repository dispatch - [" + Action + "](" + WorkflowURL + ")";
					}
					else if(Event.equals("Schedule"))
					{
						String Trigger_Actor = (String) System.getenv("GITHUB_ACTOR");
						String Workflow = (String) System.getenv("GITHUB_WORKFLOW");
						String WorkflowID = (String) System.getenv("GITHUB_RUN_ID");
						String WorkflowURL = RepositoryURL + "/actions/runs/" + WorkflowID;
						message = "[" + Trigger_Actor + "](" + ServerURL + Trigger_Actor + ") has scheduled a workflow [" + Workflow + "](" + WorkflowURL  + ")";
					}
					else if(Event.equals("Status"))
					{
						String Trigger_Actor = (String) System.getenv("GITHUB_ACTOR");
						String Workflow = (String) System.getenv("GITHUB_WORKFLOW");
						String WorkflowID = (String) System.getenv("GITHUB_RUN_ID");
						String Status = (String) System.getenv("STATUS");
						String WorkflowURL = RepositoryURL + "/actions/runs/" + WorkflowID;
						message = "The status of the [" + Workflow + "](" + WorkflowURL + ") workflow has been updated as " + Status;
					}
					else if(Event.equals("Watch"))
					{
						String Watcher = (String) System.getenv("GITHUB_ACTOR");
						message = "[" + Watcher + "](" + ServerURL + Watcher + ") has pushed the [" + Repository + "](" + RepositoryURL + ") repository under the Watch category";
					}
					else if(Event.equals("Workflow Dispatch"))
					{
						String Dispatcher = (String) System.getenv("GITHUB_ACTOR");
						String Workflow = (String) System.getenv("GITHUB_WORKFLOW");
						String WorkflowID = (String) System.getenv("GITHUB_RUN_ID");
						String WorkflowURL = RepositoryURL + "/actions/runs/" + WorkflowID;
						message = "[" + Dispatcher + "](" + ServerURL + Dispatcher + ") has triggered the [" + Workflow + "](" + WorkflowURL  + ") workflow";
					}
				}
				else
				{
					message = message.replace("(me)","[" + Actor + "](" + ActorURL + ")");
					message = message.replace("(repo)","[" + Repository + "](" + RepositoryURL + ")" );
					if(Event.equals("Create") || Event.equals("Delete"))
						Event = Event + "d";
					message = message.replace("(event)","*" + Event + "*");
					message = message.replace("(action)",Action);
					message = message.replace("(ref)",(String) System.getenv("GITHUB_REF_TYPE") + " " + System.getenv("GITHUB_REF_NAME"));
					message = message.replace("(workflow)",(String) System.getenv("GITHUB_WORKFLOW"));
					if(System.getenv("BRANCH_RULE") != null)
						message = message.replace("(rule)",(String) System.getenv("BRANCH_RULE"));
					else
						message = message.replace("(rule)","");
					if(System.getenv("LABEL_NAME") != null)
						message = message.replace("(label)",(String) System.getenv("LABEL_NAME"));
					if(System.getenv("MILESTONE") != null)
						message = message.replace("(milestone)",(String) System.getenv("MILESTONE"));
					else
						message = message.replace("(milestone)","");
					if(System.getenv("RELEASE_NAME") != null)
						message = message.replace("(release)",(String) System.getenv("RELEASE_NAME"));
					else
						message = message.replace("(release)","");
					if(System.getenv("REGISTRY_PACKAGE_NAME") != null)
						message = message.replace("(package)",(String) System.getenv("REGISTRY_PACKAGE_NAME"));
					else
						message = message.replace("(package)","");
					if(System.getenv("PULL_REQUEST_TITLE") != null)
						message = message.replace("(pull)",(String) System.getenv("PULL_REQUEST_TITLE"));
					if(System.getenv("ISSUE_TITLE") != null && Event.equals("issue_comment") && ((String)System.getenv("ISSUE_TYPE")).equals("PULL_REQUEST"))
						message = message.replace("(pull)",(String) System.getenv("ISSUE_TITLE"));
					else
						message = message.replace("(pull)","");
					if(System.getenv("ISSUE_TITLE") != null)
						message	= message.replace("(issue)",(String) System.getenv("ISSUE_TITLE"));
					else
						message = message.replace("(issue)","");
					if(System.getenv("CHECK_RUN_NAME") != null)
						message = message.replace("(run)",(String) System.getenv("CHECK_RUN_NAME"));
					else
						message = message.replace("(run)","");
					if(System.getenv("DEPLOYMENT_ENV") != null)
						message = message.replace("(deployment)",(String) System.getenv("DEPLOYMENT_ENV"));
					else
						message = message.replace("(deployment)","");
					if(System.getenv("STATUS") != null)
						message = message.replace("(status)",(String) System.getenv("STATUS"));
					else
						message = message.replace("(status)","");
					if(System.getenv("BRANCH_NAME") != null)
						message = message.replace("(branch)", (String) System.getenv("BRANCH_TYPE") + " " + System.getenv("BRANCH_NAME"));
					else
						message = message.replace("(branch)","");
					if(System.getenv("DISCUSSION") != null)
						message = message.replace("(discussion)",(String) System.getenv("DISCUSSION"));
					else
						message = message.replace("(discussion)","");
					if(System.getenv("CATEGORY_NAME") != null)
						message = message.replace("(category)", (String) System.getenv("CATEGORY_NAME"));
					else
						message = message.replace("(category)","");
					if(System.getenv("ASSIGNED_USER") != null)
						message = message.replace("(assignee)", (String) System.getenv("ASSIGNED_USER"));
					else
						message = message.replace("(assignee)","");
					if(System.getenv("ASSIGNED_LABEL") != null)
						message = message.replace("(label)", (String) System.getenv("ASSIGNED_LABEL"));
					else
						message = message.replace("(label)","");
				}
				ArrayList<String> messages = new ArrayList<String>();
				for(int i = 0 ; i < message.length() ;)
				{
				  String split_message;
				  if(i+MAX_MESSAGE_LENGTH < message.length())
				  {
				    split_message = message.substring(i,i+MAX_MESSAGE_LENGTH);
				    int displaced_length = MAX_MESSAGE_LENGTH;
				    if(split_message.contains(MESSAGE_BREAK))
				    {
				      displaced_length = split_message.lastIndexOf(MESSAGE_BREAK) + 2;
				      split_message = message.substring(i,i+displaced_length);
				      split_message = split_message.replaceAll("\\\\n","");
				    }
				    else if(split_message.contains("\n"))
				    {
				      displaced_length = split_message.lastIndexOf("\n") + 1;
				      split_message = message.substring(i,i+displaced_length);
				    }
				    else if(split_message.contains("."))
				    {
				      displaced_length = split_message.lastIndexOf(".") + 1;
				      split_message = message.substring(i,i+displaced_length);
				    }
				    i += displaced_length;
				  }
				  else
				  {
				    split_message = message.substring(i,message.length());
				    i+= MAX_MESSAGE_LENGTH;
				  }
				  messages.add(split_message);
				}

				String eventNameRaw = (String) System.getenv("GITHUB_EVENT_NAME");
				String issueTypeRaw = (String) System.getenv("ISSUE_TYPE");
				boolean isPullRequestCommentEvent = "issue_comment".equals(eventNameRaw) && "PULL_REQUEST".equals(issueTypeRaw);
				boolean isPullRequestReviewEvent = "pull_request_review".equals(eventNameRaw);
				boolean isPullRequestReviewCommentEvent = "pull_request_review_comment".equals(eventNameRaw);
				boolean isPrEvent = "pull_request".equals(eventNameRaw)
					|| "pull_request_target".equals(eventNameRaw)
					|| isPullRequestCommentEvent
					|| isPullRequestReviewEvent
					|| isPullRequestReviewCommentEvent;
				String prNumber = (String) System.getenv("PULL_REQUEST_NUMBER");
				if((prNumber == null || prNumber.isBlank()) && isPullRequestCommentEvent)
				{
					prNumber = (String) System.getenv("ISSUE_NUMBER");
				}
				String githubToken = (String) System.getenv("GITHUB_TOKEN");
				debug("EventNameRaw=" + eventNameRaw + ", ActionRaw=" + ActionRaw + ", isPrEvent=" + isPrEvent + ", isPullRequestCommentEvent=" + isPullRequestCommentEvent + ", isPullRequestReviewEvent=" + isPullRequestReviewEvent + ", isPullRequestReviewCommentEvent=" + isPullRequestReviewCommentEvent + ", prNumber=" + prNumber + ", hasGithubToken=" + (githubToken != null && !githubToken.isBlank()));
				String pullRequestTitleRaw = (String) System.getenv("PULL_REQUEST_TITLE");
				String pullRequestBodyRaw = (String) System.getenv("PULL_REQUEST_BODY");
				String pullRequestUrlRaw = (String) System.getenv("PULL_REQUEST_URL");
				String pullRequestDiffUrlRaw = (String) System.getenv("PULL_REQUEST_DIFF_URL");
				String pullRequestBaseShaRaw = (String) System.getenv("PULL_REQUEST_BASE_SHA");
				String pullRequestHeadShaRaw = (String) System.getenv("PULL_REQUEST_HEAD_SHA");
				String prLabelsRaw = (String) System.getenv("PR_LABELS");
				// Previous storage mode was PR marker comments ("comment").
				// Keep default as comment so existing behavior remains backward-compatible.
				String threadStorageMode = defaultIfBlank((String) System.getenv("CLIQ_THREAD_STORAGE_MODE"), "comment").trim().toLowerCase();
				String projectOwnerRaw = defaultIfBlank((String) System.getenv("GITHUB_PROJECT_OWNER"), "");
				String projectNumberRaw = defaultIfBlank((String) System.getenv("GITHUB_PROJECT_NUMBER"), "");
				String projectThreadFieldIdRaw = defaultIfBlank((String) System.getenv("GITHUB_PROJECT_THREAD_FIELD_ID"), "");
				String projectThreadFieldNameRaw = defaultIfBlank((String) System.getenv("GITHUB_PROJECT_THREAD_FIELD_NAME"), "Cliq Thread ID");
				String prThreadId = null;
				if(isPrEvent && prNumber != null && !prNumber.isBlank() && githubToken != null && !githubToken.isBlank())
				{
				  prThreadId = fetchCliqThreadId(
					  Repository,
					  prNumber,
					  githubToken,
					  threadStorageMode,
					  projectOwnerRaw,
					  projectNumberRaw,
					  projectThreadFieldIdRaw,
					  projectThreadFieldNameRaw
				  );
				  debug("Fetched existing PR threadId=" + prThreadId + ", storageMode=" + threadStorageMode);
				}
				String createdThreadId = null;

				for(String msg : messages)
				{
				  msg = msg.replace("\"","'");
				  String localResponse = "";
				  boolean postedInThread = false;
				  if(prThreadId != null && !prThreadId.isBlank())
				  {
					ArrayList<String> threadMessageIdCandidates = buildReplyToCandidates(prThreadId);
					for(String threadMessageIdCandidate : threadMessageIdCandidates)
					{
						HttpResult threadedResult = postJson(CliqChannelLink, buildCliqPayload(msg, GitHubInformerURL, threadMessageIdCandidate));
						status = threadedResult.status;
						localResponse = threadedResult.body;
						debug("Cliq threaded post status=" + status + ", threadMessageIdCandidate=" + threadMessageIdCandidate + ", responsePreview=" + preview(localResponse));
						responseContent.append(localResponse);
						if(status <= 299)
						{
							postedInThread = true;
							break;
						}
					}
				  }
				  else
				  {
					HttpResult directResult = postJson(CliqChannelLink, buildCliqPayload(msg, GitHubInformerURL, null));
					status = directResult.status;
					localResponse = directResult.body;
					debug("Cliq post status=" + status + ", usingReplyTo=false, responsePreview=" + preview(localResponse));
					responseContent.append(localResponse);
				  }

				  // Fallback: if all threaded attempts fail, retry as normal channel message.
				  if(!postedInThread && prThreadId != null && !prThreadId.isBlank())
				  {
					HttpResult fallbackResult = postJson(CliqChannelLink, buildCliqPayload(msg, GitHubInformerURL, null));
					status = fallbackResult.status;
					localResponse = fallbackResult.body;
					debug("Fallback normal post status=" + status + ", responsePreview=" + preview(localResponse));
					responseContent.append(localResponse);
				  }

				  if(isPrEvent && "opened".equals(ActionRaw) && (createdThreadId == null || createdThreadId.isBlank()))
				  {
					String extractedId = extractCliqMessageId(localResponse);
					if(extractedId != null && !extractedId.isBlank())
					{
					  createdThreadId = extractedId;
					  debug("Extracted createdThreadId from Cliq response=" + createdThreadId);
					}
					else
					{
					  debug("Could not extract thread/message id from Cliq response on PR opened event.");
					}
				  }

				  if(status != 204)
				    ERROR_MESSAGE = responseContent.toString();
				}

				if(isPrEvent && "opened".equals(ActionRaw) && createdThreadId != null && !createdThreadId.isBlank() && prNumber != null && !prNumber.isBlank() && githubToken != null && !githubToken.isBlank())
				{
				  boolean threadSaved = upsertCliqThreadId(
					  Repository,
					  prNumber,
					  githubToken,
					  createdThreadId,
					  threadStorageMode,
					  projectOwnerRaw,
					  projectNumberRaw,
					  projectThreadFieldIdRaw,
					  projectThreadFieldNameRaw
				  );
				  if(!threadSaved)
				  {
					System.err.println("PR thread marker not saved in any storage backend. Check workflow permissions issues:write/pull-requests:write/projects:write and token scope.");
				  }
				}
				else if(isPrEvent && "opened".equals(ActionRaw) && (createdThreadId == null || createdThreadId.isBlank()))
				{
				  System.err.println("PR thread marker not saved: Cliq response did not return a message/thread id.");
				}

				if(isPrEvent)
				{
					String aiThreadId = prThreadId;
					if((aiThreadId == null || aiThreadId.isBlank()) && createdThreadId != null && !createdThreadId.isBlank())
						aiThreadId = createdThreadId;
					handleAiReviewGate(
						Repository,
						prNumber,
						eventNameRaw,
						ActionRaw,
						prLabelsRaw,
						pullRequestTitleRaw,
						pullRequestBodyRaw,
						pullRequestUrlRaw,
						pullRequestDiffUrlRaw,
						pullRequestBaseShaRaw,
						pullRequestHeadShaRaw,
						githubToken,
						CliqChannelLink,
						aiThreadId,
						GitHubInformerURL
					);
				}
				debug("Final message status=" + status + ", errorMessagePreview=" + preview(ERROR_MESSAGE));
			}
			var githubOutput = (String) System.getenv("GITHUB_OUTPUT");
			if(Objects.nonNull(githubOutput))
			    GITHUB_ERROR = false;
			if(status == 204 || status == 200 || status == 201)
			  MESSAGE_SEND_FAILURE_ERROR = false;
			if(INVALID_ENDPOINT_ERROR)
			  ERROR_MESSAGE = "Invalid Endpoint. Endpoint must be of format : <Zoho Cliq Channel API Endpoint>?zapikey=<Zoho Cliq Webhook Token>";
			else if(GITHUB_ERROR)
			  ERROR_MESSAGE = "Environmental Variable GITHUB_OUTPUT missing";
			else if(MESSAGE_SEND_FAILURE_ERROR)
			  ERROR_MESSAGE = responseContent.toString().isBlank() ? ERROR_MESSAGE : responseContent.toString();
			else if(status == 204 || status == 200 || status == 201)
			  ERROR_MESSAGE = "GitHub Informer executed Successfully";
			writeGithubOutput(status,ERROR_MESSAGE);
		}  catch (MalformedURLException e) {
			ERROR_MESSAGE = "Invalid Endpoint URL. Please provide channel-endpoint as <Cliq Channel API Endpoint>?zapikey=<Cliq Webhook Token>";
			e.printStackTrace();
		} catch (IOException e) {
			ERROR_MESSAGE = "I/O Error while sending message to Cliq: " + e.getMessage();
			e.printStackTrace();
		} catch (Exception e) {
			ERROR_MESSAGE = "Runtime Error: " + e.getClass().getSimpleName() + " - " + e.getMessage();
			e.printStackTrace();
		}
		finally
		{
		  try
		  {
		    var githubOutput = (String) System.getenv("GITHUB_OUTPUT");
		    if(githubOutput == null || githubOutput.isBlank())
		    {
		      System.err.println("GITHUB_OUTPUT is missing. Last error: " + ERROR_MESSAGE);
		      System.exit(1);
		    }
		    var file = Path.of(githubOutput);
		    if(file.getParent() != null) Files.createDirectories(file.getParent());
		    if(MESSAGE_SEND_FAILURE_ERROR)
		    {
		      if(ERROR_MESSAGE == null || ERROR_MESSAGE.isBlank() || ERROR_MESSAGE.equals("Multiple Errors Occured"))
		      {
		        ERROR_MESSAGE = "Unknown Error Occured : Multiple Errors Occured";
		      }
		    }
		    writeGithubOutput(status,ERROR_MESSAGE);
		  }
		  catch(Exception e)
		  {
		    ERROR_MESSAGE = "Sorry we couldn't process your request due to a technical error. Please Try again later.";
		    System.err.println("Unknown Error Occured : " + ERROR_MESSAGE);
		    System.exit(1);
		  }
		}
	}
	
	// To Split and Seperate the Message from the JSON
	public static String splitMessage(String JSON)
	{
	  JSON = JSON.substring(JSON.indexOf("{"), JSON.indexOf("}"));
	  String[] JSONArray = JSON.split(",");
	  for(String s : JSONArray)
	    if(s.contains("\"message\":"))
	      return s.substring(s.indexOf(":")+1,s.length());
	  return "Error Description not Provided";
	}
	
	// used to write a Github Output so that the Shell Runner can Read
	public static void writeGithubOutput(Integer Status , String ErrorMessage) throws IOException
	{
	  var githubOutput = (String) System.getenv("GITHUB_OUTPUT");
    var file = Path.of(githubOutput);
	  var lines = ("message-status=" + Status).lines().toList();
		Files.write(file, lines, UTF_8 , CREATE , APPEND , WRITE);
		lines = ("error-message=" + ErrorMessage).lines().toList();
		Files.write(file, lines, UTF_8 , CREATE , APPEND , WRITE);
	}

	//to Split JSON for Single Line Key Value Pairs
    public static String[] LineBreaker(String Line)
    { 
        Boolean isBetweenQuotes = false;
        Integer count = 0;
        Integer startindex = 0;
        Character prec = '_';
        Integer len = 0;
        String key = new String();
        String value = new String();
        for (Character c : Line.toCharArray())
        {
            if(prec != '\\' && c == '"')
            {
                isBetweenQuotes = !isBetweenQuotes;
                if(isBetweenQuotes)
                    startindex = len;
                else
                {
                    if(count % 4 == 0)
                    {
                        key = Line.substring(startindex+1,len);
                    }
                    else if(count % 4 == 1)
                    {
                        value = Line.substring(startindex+1,Line.lastIndexOf("\""));
                    }
                    count++;
                }
            }
            prec = c;
            len++;
        }
        String[] Array = new String[2];
        if(key != "" && value != "")
        {
            Array[0] = key;
            Array[1] = value;
        }
        return Array;
    }

	public static class HttpResult
	{
		public int status;
		public String body;

		public HttpResult(int status, String body)
		{
			this.status = status;
			this.body = body == null ? "" : body;
		}
	}

	public static HttpResult postJson(String endpoint, String payload) throws IOException
	{
		debug("POST endpoint=" + endpoint + ", payloadPreview=" + preview(payload));
		HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		try (OutputStream os = connection.getOutputStream())
		{
			os.write(payload.getBytes(UTF_8));
			os.flush();
		}
		int status = connection.getResponseCode();
		String body = readConnectionBody(connection, status > 299);
		debug("POST response status=" + status + ", bodyPreview=" + preview(body));
		return new HttpResult(status, body);
	}

	public static HttpResult sendHttpRequest(String method, String endpoint, String payload, Map<String, String> headers) throws IOException
	{
		debug(method + " endpoint=" + endpoint + ", payloadPreview=" + preview(payload));
		String currentEndpoint = endpoint;
		for(int redirectCount = 0; redirectCount < 5; redirectCount++)
		{
			HttpURLConnection connection = (HttpURLConnection) new URL(currentEndpoint).openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod(method);
			if(headers != null)
			{
				for(Map.Entry<String, String> header : headers.entrySet())
				{
					if(header.getValue() != null && !header.getValue().isBlank())
						connection.setRequestProperty(header.getKey(), header.getValue());
				}
			}
			if(payload != null)
			{
				connection.setDoOutput(true);
				try (OutputStream os = connection.getOutputStream())
				{
					os.write(payload.getBytes(UTF_8));
					os.flush();
				}
			}
			int status = connection.getResponseCode();
			if(status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_SEE_OTHER || status == 307 || status == 308)
			{
				String location = connection.getHeaderField("Location");
				if(location != null && !location.isBlank())
				{
					currentEndpoint = new URL(new URL(currentEndpoint), location).toString();
					debug(method + " redirecting to=" + currentEndpoint);
					continue;
				}
			}
			String body = readConnectionBody(connection, status > 299);
			debug(method + " response status=" + status + ", bodyPreview=" + preview(body));
			return new HttpResult(status, body);
		}
		debug(method + " exceeded redirect limit for endpoint=" + endpoint);
		return new HttpResult(500, "");
	}

	public static String readConnectionBody(HttpURLConnection connection, boolean errorStream) throws IOException
	{
		if(errorStream && connection.getErrorStream() == null)
			return "";
		if(!errorStream && connection.getInputStream() == null)
			return "";
		StringBuilder response = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream ? connection.getErrorStream() : connection.getInputStream())))
		{
			String line;
			while((line = reader.readLine()) != null)
			{
				response.append(line);
			}
		}
		return response.toString();
	}

	public static String buildCliqPayload(String message, String imageUrl, String threadMessageId)
	{
		StringBuilder payload = new StringBuilder();
		payload.append("{\n\"card\":{\"theme\":\"modern-inline\"},");
		payload.append("\n\"text\":\"").append(jsonEscape(message)).append("\",");
		payload.append("\n\"sync_message\":true,");
		if(threadMessageId != null && !threadMessageId.isBlank())
		{
			String normalizedThreadMessageId = normalizeCliqReplyToId(threadMessageId);
			payload.append("\n\"thread_message_id\":\"").append(jsonEscape(normalizedThreadMessageId)).append("\",");
			payload.append("\n\"post_in_parent\":false,");
		}
		payload.append("\n\"bot\":\n{\n\"name\":\"GitHub Informer for Zoho Cliq\",\n\"image\":\"").append(jsonEscape(imageUrl)).append("\"}}\n");
		return payload.toString();
	}

	public static String buildCliqCardPayload(String message, String imageUrl, String threadMessageId)
	{
		StringBuilder payload = new StringBuilder();
		payload.append("{\n\"card\":{\"theme\":\"modern-inline\"},");
		payload.append("\n\"text\":\"").append(jsonEscape(message)).append("\",");
		payload.append("\n\"sync_message\":true,");
		if(threadMessageId != null && !threadMessageId.isBlank())
		{
			String normalizedThreadMessageId = normalizeCliqReplyToId(threadMessageId);
			payload.append("\n\"thread_message_id\":\"").append(jsonEscape(normalizedThreadMessageId)).append("\",");
			payload.append("\n\"post_in_parent\":false,");
		}
		payload.append("\n\"bot\":\n{\n\"name\":\"GitHub Informer for Zoho Cliq\",\n\"image\":\"").append(jsonEscape(imageUrl)).append("\"}}\n");
		return payload.toString();
	}

	public static String normalizeCliqReplyToId(String rawReplyToId)
	{
		if(rawReplyToId == null)
			return "";
		String trimmed = rawReplyToId.trim();
		if(trimmed.isBlank())
			return "";
		try
		{
			String decoded = URLDecoder.decode(trimmed, UTF_8);
			if(decoded != null && !decoded.isBlank())
			{
				debug("Normalized reply_to id for threaded post.");
				return decoded;
			}
		}
		catch(Exception e)
		{
			debug("Unable to decode reply_to id, using raw value.");
		}
		return trimmed;
	}

	public static ArrayList<String> buildReplyToCandidates(String rawReplyToId)
	{
		ArrayList<String> candidates = new ArrayList<String>();
		if(rawReplyToId == null)
			return candidates;
		String trimmed = rawReplyToId.trim();
		if(trimmed.isBlank())
			return candidates;

		addUnique(candidates, trimmed);

		String decoded = trimmed;
		try
		{
			decoded = URLDecoder.decode(trimmed, UTF_8);
			addUnique(candidates, decoded);
		}
		catch(Exception e)
		{
			debug("Unable to decode reply_to id while building candidates.");
		}

		try
		{
			String encodedFromDecoded = URLEncoder.encode(decoded, UTF_8).replace("+", "%20");
			addUnique(candidates, encodedFromDecoded);
		}
		catch(Exception e)
		{
			debug("Unable to URL encode decoded reply_to candidate.");
		}

		try
		{
			String encodedFromTrimmed = URLEncoder.encode(trimmed, UTF_8).replace("+", "%20");
			addUnique(candidates, encodedFromTrimmed);
		}
		catch(Exception e)
		{
			debug("Unable to URL encode raw reply_to candidate.");
		}

		debug("Built reply_to candidates count=" + candidates.size());
		return candidates;
	}

	public static void addUnique(ArrayList<String> items, String value)
	{
		if(value == null)
			return;
		String normalized = value.trim();
		if(normalized.isBlank())
			return;
		if(!items.contains(normalized))
			items.add(normalized);
	}

	public static String jsonEscape(String raw)
	{
		if(raw == null)
			return "";
		return raw.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
	}

	public static String extractCliqMessageId(String response)
	{
		if(response == null || response.isBlank())
			return null;
		String[] keys = new String[] {"thread_id", "threadId", "message_id", "messageId", "id"};
		for(String key : keys)
		{
			Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]+)\"");
			Matcher m = p.matcher(response);
			if(m.find())
				return m.group(1);
		}
		return null;
	}

	public static String fetchCliqThreadId(String repository, String prNumber, String githubToken, String storageMode, String projectOwner, String projectNumberRaw, String projectThreadFieldId, String projectThreadFieldName)
	{
		if("project".equalsIgnoreCase(defaultIfBlank(storageMode, "comment")))
		{
			String projectThreadId = fetchCliqThreadIdFromProjectField(repository, prNumber, githubToken, projectOwner, projectNumberRaw, projectThreadFieldName);
			if(projectThreadId != null && !projectThreadId.isBlank())
				return projectThreadId;
			debug("Project field storage did not return thread id. Falling back to PR marker comment lookup.");
		}
		// Previous behavior (disabled): read thread id from PR marker comment.
		// return fetchCliqThreadIdFromPRComments(repository, prNumber, githubToken);
		return null;
	}

	public static boolean upsertCliqThreadId(String repository, String prNumber, String githubToken, String threadId, String storageMode, String projectOwner, String projectNumberRaw, String projectThreadFieldId, String projectThreadFieldName)
	{
		if("project".equalsIgnoreCase(defaultIfBlank(storageMode, "comment")))
		{
			boolean savedInProject = upsertCliqThreadIdInProjectField(repository, prNumber, githubToken, threadId, projectOwner, projectNumberRaw, projectThreadFieldId, projectThreadFieldName);
			if(savedInProject)
				return true;
			debug("Project field write failed. Falling back to PR marker comment write.");
		}
		// Previous behavior (disabled): save thread id as a hidden PR marker comment.
		// return upsertCliqThreadIdComment(repository, prNumber, githubToken, threadId);
		return false;
	}

	public static String fetchCliqThreadIdFromPRComments(String repository, String prNumber, String githubToken)
	{
		try
		{
			String url = "https://api.github.com/repos/" + repository + "/issues/" + prNumber + "/comments?per_page=100";
			debug("Fetching PR comments for marker from " + url);
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/vnd.github+json");
			connection.setRequestProperty("Authorization", "Bearer " + githubToken);
			int status = connection.getResponseCode();
			String body = readConnectionBody(connection, status > 299);
			debug("Fetch comments status=" + status + ", bodyPreview=" + preview(body));
			if(status > 299 || body == null || body.isBlank())
				return null;

			Pattern markerPattern = Pattern.compile("cliq-thread-id:([^\\s<]+)");
			Matcher markerMatcher = markerPattern.matcher(body);
			if(markerMatcher.find())
			{
				debug("Existing marker found in PR comments.");
				return markerMatcher.group(1);
			}
			debug("No existing marker found in PR comments.");
		}
		catch(Exception e)
		{
			System.err.println("Unable to fetch PR thread marker: " + e.getMessage());
		}
		return null;
	}

	public static boolean upsertCliqThreadIdComment(String repository, String prNumber, String githubToken, String threadId)
	{
		try
		{
			String url = "https://api.github.com/repos/" + repository + "/issues/" + prNumber + "/comments";
			String bodyText = "<!-- cliq-thread-id:" + threadId + " -->\\nCliq thread marker for GitHub Informer.";
			String payload = "{\"body\":\"" + jsonEscape(bodyText) + "\"}";
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/vnd.github+json");
			connection.setRequestProperty("Authorization", "Bearer " + githubToken);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			try (OutputStream os = connection.getOutputStream())
			{
				os.write(payload.getBytes(UTF_8));
				os.flush();
			}
			int status = connection.getResponseCode();
			String body = readConnectionBody(connection, status > 299);
			debug("Create PR marker comment status=" + status + ", bodyPreview=" + preview(body));
			if(status >= 200 && status <= 299)
				return true;
			System.err.println("Unable to save PR thread marker: status=" + status + ", body=" + preview(body));
			return false;
		}
		catch(Exception e)
		{
			System.err.println("Unable to save PR thread marker: " + e.getMessage());
			return false;
		}
	}

	public static class ProjectItemContext
	{
		public String itemId;
		public String projectId;
		public String fieldValue;

		public ProjectItemContext(String itemId, String projectId, String fieldValue)
		{
			this.itemId = itemId;
			this.projectId = projectId;
			this.fieldValue = fieldValue;
		}
	}

	public static String fetchCliqThreadIdFromProjectField(String repository, String prNumber, String githubToken, String projectOwner, String projectNumberRaw, String projectThreadFieldName)
	{
		ProjectItemContext context = resolveProjectItemContext(repository, prNumber, githubToken, projectOwner, projectNumberRaw, projectThreadFieldName);
		if(context == null)
			return null;
		String value = defaultIfBlank(context.fieldValue, "").trim();
		if(value.isBlank())
			return null;
		debug("Found Cliq thread id in project field storage.");
		return value;
	}

	public static boolean upsertCliqThreadIdInProjectField(String repository, String prNumber, String githubToken, String threadId, String projectOwner, String projectNumberRaw, String projectThreadFieldId, String projectThreadFieldName)
	{
		try
		{
			ProjectItemContext context = resolveProjectItemContext(repository, prNumber, githubToken, projectOwner, projectNumberRaw, projectThreadFieldName);
			if(context == null || context.projectId == null || context.projectId.isBlank() || context.itemId == null || context.itemId.isBlank())
			{
				System.err.println("Project thread storage skipped: unable to resolve project/item context.");
				return false;
			}

			String fieldId = defaultIfBlank(projectThreadFieldId, "").trim();
			if(fieldId.isBlank())
			{
				fieldId = resolveProjectFieldIdByName(githubToken, context.projectId, projectThreadFieldName);
			}
			if(fieldId == null || fieldId.isBlank())
			{
				System.err.println("Project thread storage skipped: unable to resolve project field id.");
				return false;
			}

			String mutation = "mutation($projectId:ID!,$itemId:ID!,$fieldId:ID!,$value:String!){updateProjectV2ItemFieldValue(input:{projectId:$projectId,itemId:$itemId,fieldId:$fieldId,value:{text:$value}}){projectV2Item{id}}}";
			String payload = "{"
				+ "\"query\":\"" + jsonEscape(mutation) + "\"," 
				+ "\"variables\":{"
				+ "\"projectId\":\"" + jsonEscape(context.projectId) + "\"," 
				+ "\"itemId\":\"" + jsonEscape(context.itemId) + "\"," 
				+ "\"fieldId\":\"" + jsonEscape(fieldId) + "\"," 
				+ "\"value\":\"" + jsonEscape(threadId) + "\""
				+ "}}";

			HttpResult response = postGitHubGraphql(githubToken, payload);
			if(response.status >= 200 && response.status <= 299 && !defaultIfBlank(response.body, "").contains("\"errors\""))
			{
				debug("Saved Cliq thread id in GitHub Project custom field.");
				return true;
			}
			System.err.println("Unable to write Cliq thread id to project field: status=" + response.status + ", body=" + preview(response.body));
		}
		catch(Exception e)
		{
			System.err.println("Unable to write Cliq thread id to project field: " + e.getMessage());
		}
		return false;
	}

	public static ProjectItemContext resolveProjectItemContext(String repository, String prNumberRaw, String githubToken, String projectOwnerRaw, String projectNumberRaw, String projectThreadFieldNameRaw)
	{
		try
		{
			String owner = defaultIfBlank(projectOwnerRaw, "").trim();
			String projectNumberText = defaultIfBlank(projectNumberRaw, "").trim();
			String fieldName = defaultIfBlank(projectThreadFieldNameRaw, "Cliq Thread ID").trim();
			if(owner.isBlank() || projectNumberText.isBlank())
			{
				debug("Project storage is not configured: missing GITHUB_PROJECT_OWNER or GITHUB_PROJECT_NUMBER.");
				return null;
			}

			int projectNumber;
			int prNumber;
			try
			{
				projectNumber = Integer.parseInt(projectNumberText);
				prNumber = Integer.parseInt(defaultIfBlank(prNumberRaw, "").trim());
			}
			catch(Exception e)
			{
				System.err.println("Project storage parse error: invalid project or PR number.");
				return null;
			}

			String[] repoParts = defaultIfBlank(repository, "").split("/");
			if(repoParts.length != 2)
				return null;

			String projectId = resolveProjectIdByOwnerAndNumber(githubToken, owner, projectNumber);
			if(projectId == null || projectId.isBlank())
			{
				System.err.println("Project thread storage skipped: unable to resolve project id for owner/number.");
				return null;
			}

			String pullRequestNodeId = resolvePullRequestNodeId(githubToken, repoParts[0], repoParts[1], prNumber);
			if(pullRequestNodeId == null || pullRequestNodeId.isBlank())
			{
				System.err.println("Project thread storage skipped: unable to resolve pull request node id.");
				return null;
			}

			ProjectItemContext existingContext = resolveProjectItemContextFromPullRequestNode(githubToken, pullRequestNodeId, owner, projectNumber, fieldName);
			if(existingContext != null && existingContext.itemId != null && !existingContext.itemId.isBlank())
				return existingContext;

			String addedItemId = addPullRequestToProject(githubToken, projectId, pullRequestNodeId);
			if(addedItemId != null && !addedItemId.isBlank())
				return new ProjectItemContext(addedItemId, projectId, "");

			// One more lookup in case PR was already added concurrently.
			ProjectItemContext contextAfterAdd = resolveProjectItemContextFromPullRequestNode(githubToken, pullRequestNodeId, owner, projectNumber, fieldName);
			if(contextAfterAdd != null && contextAfterAdd.itemId != null && !contextAfterAdd.itemId.isBlank())
				return contextAfterAdd;
		}
		catch(Exception e)
		{
			System.err.println("Unable to resolve project item context: " + e.getMessage());
		}
		return null;
	}

	public static String resolveProjectIdByOwnerAndNumber(String githubToken, String owner, int projectNumber)
	{
		try
		{
			String query = "query($owner:String!,$projectNumber:Int!){organization(login:$owner){projectV2(number:$projectNumber){id}} user(login:$owner){projectV2(number:$projectNumber){id}}}";
			String payload = "{"
				+ "\"query\":\"" + jsonEscape(query) + "\","
				+ "\"variables\":{"
				+ "\"owner\":\"" + jsonEscape(owner) + "\","
				+ "\"projectNumber\":" + projectNumber
				+ "}}";
			HttpResult response = postGitHubGraphql(githubToken, payload);
			if(response.status < 200 || response.status > 299 || response.body == null || response.body.isBlank() || response.body.contains("\"errors\""))
				return "";
			Matcher matcher = Pattern.compile("\\\"projectV2\\\":\\{\\\"id\\\":\\\"([^\\\"]+)\\\"\\}").matcher(response.body);
			if(matcher.find())
				return matcher.group(1);
		}
		catch(Exception e)
		{
			System.err.println("Unable to resolve project id: " + e.getMessage());
		}
		return "";
	}

	public static String resolvePullRequestNodeId(String githubToken, String repoOwner, String repoName, int prNumber)
	{
		try
		{
			String query = "query($owner:String!,$repo:String!,$prNumber:Int!){repository(owner:$owner,name:$repo){pullRequest(number:$prNumber){id}}}";
			String payload = "{"
				+ "\"query\":\"" + jsonEscape(query) + "\","
				+ "\"variables\":{"
				+ "\"owner\":\"" + jsonEscape(repoOwner) + "\","
				+ "\"repo\":\"" + jsonEscape(repoName) + "\","
				+ "\"prNumber\":" + prNumber
				+ "}}";
			HttpResult response = postGitHubGraphql(githubToken, payload);
			if(response.status < 200 || response.status > 299 || response.body == null || response.body.isBlank() || response.body.contains("\"errors\""))
				return "";
			Matcher matcher = Pattern.compile("\\\"pullRequest\\\":\\{\\\"id\\\":\\\"([^\\\"]+)\\\"").matcher(response.body);
			if(matcher.find())
				return matcher.group(1);
		}
		catch(Exception e)
		{
			System.err.println("Unable to resolve pull request node id: " + e.getMessage());
		}
		return "";
	}

	public static ProjectItemContext resolveProjectItemContextFromPullRequestNode(String githubToken, String pullRequestNodeId, String owner, int projectNumber, String fieldName)
	{
		try
		{
			String query = "query($prId:ID!,$fieldName:String!){node(id:$prId){... on PullRequest{projectItems(first:100){nodes{id project{id number owner{login}} fieldValueByName(name:$fieldName){... on ProjectV2ItemFieldTextValue{text}}}}}}}";
			String payload = "{"
				+ "\"query\":\"" + jsonEscape(query) + "\","
				+ "\"variables\":{"
				+ "\"prId\":\"" + jsonEscape(pullRequestNodeId) + "\","
				+ "\"fieldName\":\"" + jsonEscape(fieldName) + "\""
				+ "}}";
			HttpResult response = postGitHubGraphql(githubToken, payload);
			if(response.status < 200 || response.status > 299 || response.body == null || response.body.isBlank() || response.body.contains("\"errors\""))
				return null;

			Matcher nodeMatcher = Pattern.compile("\\{\\\"id\\\":\\\"([^\\\"]+)\\\",\\\"project\\\":\\{\\\"id\\\":\\\"([^\\\"]+)\\\",\\\"number\\\":(\\d+),\\\"owner\\\":\\{\\\"login\\\":\\\"([^\\\"]+)\\\"\\}\\},\\\"fieldValueByName\\\":(null|\\{\\\"text\\\":\\\"((?:\\\\.|[^\\\\\"])*)\\\"[^\\}]*\\})\\}", Pattern.DOTALL).matcher(response.body);
			while(nodeMatcher.find())
			{
				String itemId = nodeMatcher.group(1);
				String projectId = nodeMatcher.group(2);
				int currentProjectNumber;
				try
				{
					currentProjectNumber = Integer.parseInt(nodeMatcher.group(3));
				}
				catch(Exception e)
				{
					continue;
				}
				String currentOwner = defaultIfBlank(nodeMatcher.group(4), "");
				if(currentProjectNumber != projectNumber || !owner.equalsIgnoreCase(currentOwner))
					continue;
				String value = "";
				if(nodeMatcher.group(5) != null && !"null".equals(nodeMatcher.group(5)))
					value = jsonUnescape(defaultIfBlank(nodeMatcher.group(6), ""));
				return new ProjectItemContext(itemId, projectId, value);
			}
		}
		catch(Exception e)
		{
			System.err.println("Unable to resolve project item context from PR node: " + e.getMessage());
		}
		return null;
	}

	public static String addPullRequestToProject(String githubToken, String projectId, String pullRequestNodeId)
	{
		try
		{
			String mutation = "mutation($projectId:ID!,$contentId:ID!){addProjectV2ItemById(input:{projectId:$projectId,contentId:$contentId}){item{id}}}";
			String payload = "{"
				+ "\"query\":\"" + jsonEscape(mutation) + "\","
				+ "\"variables\":{"
				+ "\"projectId\":\"" + jsonEscape(projectId) + "\","
				+ "\"contentId\":\"" + jsonEscape(pullRequestNodeId) + "\""
				+ "}}";
			HttpResult response = postGitHubGraphql(githubToken, payload);
			if(response.status < 200 || response.status > 299 || response.body == null || response.body.isBlank())
				return "";
			if(response.body.contains("\"errors\""))
			{
				debug("addProjectV2ItemById returned errors. It may already exist or token lacks project permission. bodyPreview=" + preview(response.body));
				return "";
			}
			Matcher matcher = Pattern.compile("\\\"item\\\":\\{\\\"id\\\":\\\"([^\\\"]+)\\\"\\}").matcher(response.body);
			if(matcher.find())
				return matcher.group(1);
		}
		catch(Exception e)
		{
			System.err.println("Unable to add pull request to project: " + e.getMessage());
		}
		return "";
	}

	public static String resolveProjectFieldIdByName(String githubToken, String projectId, String fieldNameRaw)
	{
		try
		{
			String fieldName = defaultIfBlank(fieldNameRaw, "Cliq Thread ID").trim();
			String query = "query($projectId:ID!){node(id:$projectId){... on ProjectV2{fields(first:100){nodes{... on ProjectV2FieldCommon{id name}}}}}}";
			String payload = "{"
				+ "\"query\":\"" + jsonEscape(query) + "\"," 
				+ "\"variables\":{\"projectId\":\"" + jsonEscape(projectId) + "\"}}";
			HttpResult response = postGitHubGraphql(githubToken, payload);
			if(response.status < 200 || response.status > 299 || response.body == null || response.body.isBlank() || response.body.contains("\"errors\""))
				return "";

			Matcher matcher = Pattern.compile("\\\"id\\\":\\\"([^\\\"]+)\\\",\\\"name\\\":\\\"((?:\\\\.|[^\\\\\"])*)\\\"").matcher(response.body);
			while(matcher.find())
			{
				String id = matcher.group(1);
				String name = jsonUnescape(defaultIfBlank(matcher.group(2), ""));
				if(id.startsWith("PVTF_") && fieldName.equalsIgnoreCase(name))
					return id;
			}
		}
		catch(Exception e)
		{
			System.err.println("Unable to resolve project field id: " + e.getMessage());
		}
		return "";
	}

	public static HttpResult postGitHubGraphql(String githubToken, String payload) throws IOException
	{
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/vnd.github+json");
		headers.put("Authorization", "Bearer " + githubToken);
		headers.put("Content-Type", "application/json");
		return sendHttpRequest("POST", "https://api.github.com/graphql", payload, headers);
	}

	public static class AiReviewDecision
	{
		public boolean passed;
		public String summary;
		public String details;

		public AiReviewDecision(boolean passed, String summary, String details)
		{
			this.passed = passed;
			this.summary = summary == null ? "" : summary;
			this.details = details == null ? "" : details;
		}
	}

	public static void handleAiReviewGate(String repository, String prNumber, String eventNameRaw, String actionRaw, String prLabelsRaw, String pullRequestTitle, String pullRequestBody, String pullRequestUrl, String pullRequestDiffUrl, String pullRequestBaseSha, String pullRequestHeadSha, String githubToken, String cliqEndpoint, String cliqThreadId, String imageUrl)
	{
		if(!isTrue(System.getenv("AI_REVIEW_ENABLED")))
			return;
		if(prNumber == null || prNumber.isBlank())
			return;

		String triggerMode = defaultIfBlank(System.getenv("AI_REVIEW_TRIGGER"), "auto").trim().toLowerCase();
		boolean runOnSync = isTrue(defaultIfBlank(System.getenv("AI_REVIEW_ON_SYNC"), "true"));
		String triggerLabel = defaultIfBlank(System.getenv("AI_REVIEW_LABEL"), "");

		if(!shouldRunAiReviewForEvent(triggerMode, triggerLabel, runOnSync, eventNameRaw, actionRaw, prLabelsRaw))
			return;

		String checkName = defaultIfBlank(System.getenv("AI_REVIEW_CHECK_NAME"), "AI Review Gate");
		AiReviewDecision decision = evaluateAiReviewDecision(repository, prNumber, pullRequestTitle, pullRequestBody, pullRequestUrl, pullRequestDiffUrl, pullRequestBaseSha, pullRequestHeadSha, githubToken);

		if(githubToken != null && !githubToken.isBlank() && pullRequestHeadSha != null && !pullRequestHeadSha.isBlank())
		{
			setAiReviewCheckRun(repository, pullRequestHeadSha, githubToken, checkName, decision.passed, decision.summary, decision.details);
		}
		else
		{
			System.err.println("AI review check run skipped: missing github token or PR head sha.");
		}

		if(!decision.passed)
		{
			String failureMessage = buildAiFailureMessage(prNumber, pullRequestUrl, decision.summary, decision.details);
			if(githubToken != null && !githubToken.isBlank())
			{
				postPullRequestComment(repository, prNumber, githubToken, failureMessage);
			}
			else
			{
				System.err.println("AI review failure PR comment skipped: missing github token.");
			}

			postAiFailureToCliqThread(cliqEndpoint, cliqThreadId, imageUrl, failureMessage);
		}
	}

	public static boolean shouldRunAiReviewForEvent(String triggerMode, String triggerLabel, boolean runOnSync, String eventNameRaw, String actionRaw, String prLabelsRaw)
	{
		if(!"pull_request".equals(eventNameRaw) && !"pull_request_target".equals(eventNameRaw))
			return false;

		if("auto".equals(triggerMode))
		{
			if("opened".equals(actionRaw) || "reopened".equals(actionRaw))
				return true;
			if("synchronize".equals(actionRaw))
				return runOnSync;
			return false;
		}

		if("label".equals(triggerMode))
		{
			if(triggerLabel == null || triggerLabel.isBlank())
				return false;
			if(!("opened".equals(actionRaw) || "reopened".equals(actionRaw) || "synchronize".equals(actionRaw) || "labeled".equals(actionRaw)))
				return false;
			if("synchronize".equals(actionRaw) && !runOnSync)
				return false;
			return hasLabel(prLabelsRaw, triggerLabel);
		}

		return false;
	}

	public static boolean hasLabel(String labelsRaw, String expectedLabel)
	{
		if(labelsRaw == null || labelsRaw.isBlank() || expectedLabel == null || expectedLabel.isBlank())
			return false;
		for(String label : labelsRaw.split("\\\\|\\\\||,|\\n"))
		{
			if(expectedLabel.trim().equalsIgnoreCase(label.trim()))
				return true;
		}
		return false;
	}

	public static AiReviewDecision evaluateAiReviewDecision(String repository, String prNumber, String pullRequestTitle, String pullRequestBody, String pullRequestUrl, String pullRequestDiffUrl, String pullRequestBaseSha, String pullRequestHeadSha, String githubToken)
	{
		String aiToken = (String) System.getenv("AI_REVIEW_TOKEN");
		String modelFromEnv = defaultIfBlank(System.getenv("AI_REVIEW_MODEL"), "");
		String apiUrlFromEnv = defaultIfBlank(System.getenv("AI_REVIEW_API_URL"), "");

		if(aiToken == null || aiToken.isBlank())
			return new AiReviewDecision(false, "AI Review Gate failed", "AI review token is missing.");
		if(githubToken == null || githubToken.isBlank())
			return new AiReviewDecision(false, "AI Review Gate failed", "GITHUB_TOKEN is missing.");

		String diff = fetchPullRequestDiff(repository, prNumber, pullRequestDiffUrl, pullRequestBaseSha, pullRequestHeadSha, githubToken);
		if(diff == null || diff.isBlank())
			return new AiReviewDecision(false, "AI Review Gate failed", "Unable to fetch PR diff from GitHub.");

		String userPrompt = buildAiPrompt(repository, prNumber, pullRequestTitle, pullRequestBody, pullRequestUrl, diff);
		String provider = detectAiProvider(aiToken, apiUrlFromEnv);
		String model = resolveModelForProvider(provider, modelFromEnv);
		String apiUrl = resolveApiUrlForProvider(provider, apiUrlFromEnv);
		String systemPrompt = "You are a strict PR reviewer. Respond with plain text only using this exact structure: RESULT: PASS or FAIL, SUMMARY: one short line, DETAILS: numbered points (1., 2., 3.). For each detail point include: FILE: <path>, LINE: <line number>, ISSUE: <what is wrong>, FIX: <what to change>. Use file paths and line numbers from the provided diff hunks. If an exact line cannot be determined, use LINE: n/a. Fail when there are critical or high severity issues related to security, data loss risk, breaking regressions, missing critical validation/error handling, or missing critical tests for changed logic.";

		try
		{
			HttpResult aiResponse = invokeAiProvider(provider, apiUrl, aiToken, model, systemPrompt, userPrompt);
			if(aiResponse.status < 200 || aiResponse.status > 299)
				return new AiReviewDecision(false, "AI Review Gate failed", provider + " request failed with status " + aiResponse.status + ".");

			String content = extractAiContent(aiResponse.body, provider);
			if(content == null || content.isBlank())
				return new AiReviewDecision(false, "AI Review Gate failed", provider + " returned empty content.");

			Boolean passedDecision = parseAiPassFail(content, aiResponse.body);
			if(passedDecision == null)
				return new AiReviewDecision(false, "AI Review Gate failed", "AI response did not include a valid RESULT field. Response preview: " + trimTo(defaultIfBlank(content, aiResponse.body), 800));

			boolean passed = passedDecision.booleanValue();
			String summary = extractLine(content, "SUMMARY");
			if(summary == null || summary.isBlank())
				summary = passed ? "AI review passed" : "AI review report";
			String details = formatAiReviewDetails(content);
			if(details == null || details.isBlank())
				details = "1. No detailed issues were returned by the AI response.";
			return new AiReviewDecision(passed, summary, details);
		}
		catch(Exception e)
		{
			return new AiReviewDecision(false, "AI Review Gate failed", provider + " error: " + e.getMessage());
		}
	}

	public static String detectAiProvider(String token, String apiUrl)
	{
		String url = defaultIfBlank(apiUrl, "").toLowerCase();
		if(url.contains("anthropic"))
			return "claude";
		if(url.contains("generativelanguage.googleapis.com") || url.contains("gemini"))
			return "gemini";
		if(url.contains("openai"))
			return "openai";

		String normalizedToken = defaultIfBlank(token, "").trim();
		if(normalizedToken.startsWith("sk-ant-"))
			return "claude";
		if(normalizedToken.startsWith("AIza"))
			return "gemini";
		if(normalizedToken.startsWith("sk-"))
			return "openai";

		// Default to OpenAI-compatible for unknown token patterns.
		return "openai";
	}

	public static String resolveModelForProvider(String provider, String configuredModel)
	{
		if(configuredModel != null && !configuredModel.isBlank())
			return configuredModel;
		if("claude".equals(provider))
			return "claude-3-5-sonnet-latest";
		if("gemini".equals(provider))
			return "gemini-1.5-pro";
		return "gpt-4.1-mini";
	}

	public static String resolveApiUrlForProvider(String provider, String configuredApiUrl)
	{
		if(configuredApiUrl != null && !configuredApiUrl.isBlank())
			return configuredApiUrl;
		if("claude".equals(provider))
			return "https://api.anthropic.com/v1/messages";
		if("gemini".equals(provider))
			return "https://generativelanguage.googleapis.com/v1beta/models";
		return "https://api.openai.com/v1/chat/completions";
	}

	public static HttpResult invokeAiProvider(String provider, String apiUrl, String token, String model, String systemPrompt, String userPrompt) throws IOException
	{
		if("claude".equals(provider))
			return invokeClaude(apiUrl, token, model, systemPrompt, userPrompt);
		if("gemini".equals(provider))
			return invokeGemini(apiUrl, token, model, systemPrompt, userPrompt);
		return invokeOpenAiCompatible(apiUrl, token, model, systemPrompt, userPrompt);
	}

	public static HttpResult invokeOpenAiCompatible(String apiUrl, String token, String model, String systemPrompt, String userPrompt) throws IOException
	{
		String payload = "{\"model\":\"" + jsonEscape(model) + "\",\"temperature\":0.1,\"messages\":[{\"role\":\"system\",\"content\":\"" + jsonEscape(systemPrompt) + "\"},{\"role\":\"user\",\"content\":\"" + jsonEscape(userPrompt) + "\"}]}";
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bearer " + token);
		return sendHttpRequest("POST", apiUrl, payload, headers);
	}

	public static HttpResult invokeClaude(String apiUrl, String token, String model, String systemPrompt, String userPrompt) throws IOException
	{
		String payload = "{\"model\":\"" + jsonEscape(model) + "\",\"max_tokens\":1200,\"temperature\":0.1,\"system\":\"" + jsonEscape(systemPrompt) + "\",\"messages\":[{\"role\":\"user\",\"content\":\"" + jsonEscape(userPrompt) + "\"}]}";
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("x-api-key", token);
		headers.put("anthropic-version", "2023-06-01");
		return sendHttpRequest("POST", apiUrl, payload, headers);
	}

	public static HttpResult invokeGemini(String apiUrl, String token, String model, String systemPrompt, String userPrompt) throws IOException
	{
		String endpoint = apiUrl;
		if(!endpoint.contains("generateContent"))
		{
			if(endpoint.endsWith("/"))
				endpoint = endpoint.substring(0, endpoint.length() - 1);
			endpoint = endpoint + "/" + urlEncodePathSegment(model) + ":generateContent";
		}
		if(endpoint.contains("?"))
			endpoint = endpoint + "&key=" + URLEncoder.encode(token, UTF_8);
		else
			endpoint = endpoint + "?key=" + URLEncoder.encode(token, UTF_8);

		String payload = "{\"system_instruction\":{\"parts\":[{\"text\":\"" + jsonEscape(systemPrompt) + "\"}]},\"contents\":[{\"parts\":[{\"text\":\"" + jsonEscape(userPrompt) + "\"}]}],\"generationConfig\":{\"temperature\":0.1}}";
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		return sendHttpRequest("POST", endpoint, payload, headers);
	}

	public static String urlEncodePathSegment(String raw)
	{
		if(raw == null)
			return "";
		return raw.replace(" ", "%20");
	}

	public static String fetchPullRequestDiff(String repository, String prNumber, String pullRequestDiffUrl, String pullRequestBaseSha, String pullRequestHeadSha, String githubToken)
	{
		try
		{
			if(pullRequestDiffUrl != null && !pullRequestDiffUrl.isBlank())
			{
				HashMap<String, String> diffHeaders = new HashMap<String, String>();
				diffHeaders.put("Accept", "application/vnd.github.v3.diff");
				diffHeaders.put("Authorization", "Bearer " + githubToken);
				HttpResult diffUrlResponse = sendHttpRequest("GET", pullRequestDiffUrl, null, diffHeaders);
				if(diffUrlResponse.status >= 200 && diffUrlResponse.status <= 299 && diffUrlResponse.body != null && !diffUrlResponse.body.isBlank())
					return diffUrlResponse.body;
			}

			if(pullRequestBaseSha != null && !pullRequestBaseSha.isBlank() && pullRequestHeadSha != null && !pullRequestHeadSha.isBlank())
			{
				HashMap<String, String> compareHeaders = new HashMap<String, String>();
				compareHeaders.put("Accept", "application/vnd.github.v3.diff");
				compareHeaders.put("Authorization", "Bearer " + githubToken);
				String compareUrl = "https://api.github.com/repos/" + repository + "/compare/" + pullRequestBaseSha + "..." + pullRequestHeadSha;
				HttpResult compareResponse = sendHttpRequest("GET", compareUrl, null, compareHeaders);
				if(compareResponse.status >= 200 && compareResponse.status <= 299 && compareResponse.body != null && !compareResponse.body.isBlank())
					return compareResponse.body;
			}

			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Accept", "application/vnd.github.v3.diff");
			headers.put("Authorization", "Bearer " + githubToken);
			HttpResult response = sendHttpRequest("GET", "https://api.github.com/repos/" + repository + "/pulls/" + prNumber, null, headers);
			if(response.status >= 200 && response.status <= 299)
				return response.body;
		}
		catch(Exception e)
		{
			System.err.println("Unable to fetch PR diff: " + e.getMessage());
		}
		return "";
	}

	public static String buildAiPrompt(String repository, String prNumber, String pullRequestTitle, String pullRequestBody, String pullRequestUrl, String diff)
	{
		StringBuilder prompt = new StringBuilder();
		prompt.append("Repository: ").append(defaultIfBlank(repository, "")).append("\\n");
		prompt.append("PR Number: ").append(defaultIfBlank(prNumber, "")).append("\\n");
		prompt.append("PR Title: ").append(defaultIfBlank(pullRequestTitle, "")).append("\\n");
		prompt.append("PR URL: ").append(defaultIfBlank(pullRequestUrl, "")).append("\\n\\n");
		prompt.append("PR Description:\\n").append(defaultIfBlank(pullRequestBody, "")).append("\\n\\n");
		prompt.append("Diff:\\n").append(trimTo(defaultIfBlank(diff, ""), 18000));
		return prompt.toString();
	}

	public static String extractAiContent(String body, String provider)
	{
		if(body == null || body.isBlank())
			return "";
		if("claude".equals(provider))
			return extractClaudeContent(body);
		if("gemini".equals(provider))
			return extractGeminiContent(body);
		return extractOpenAiContent(body);
	}

	public static String extractOpenAiContent(String body)
	{
		if(body == null || body.isBlank())
			return "";
		Matcher messageMatcher = Pattern.compile("\\\"content\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"", Pattern.DOTALL).matcher(body);
		if(messageMatcher.find())
			return jsonUnescape(messageMatcher.group(1));
		return "";
	}

	public static String extractClaudeContent(String body)
	{
		if(body == null || body.isBlank())
			return "";
		Matcher textMatcher = Pattern.compile("\\\"text\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"", Pattern.DOTALL).matcher(body);
		if(textMatcher.find())
			return jsonUnescape(textMatcher.group(1));
		return "";
	}

	public static String extractGeminiContent(String body)
	{
		if(body == null || body.isBlank())
			return "";
		Matcher textMatcher = Pattern.compile("\\\"text\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"", Pattern.DOTALL).matcher(body);
		if(textMatcher.find())
			return jsonUnescape(textMatcher.group(1));
		return "";
	}

	public static String extractLine(String content, String key)
	{
		if(content == null)
			return "";
		Matcher matcher = Pattern.compile("(?im)^\\s*" + Pattern.quote(key) + "\\s*:\\s*(.+)$").matcher(content);
		if(matcher.find())
			return matcher.group(1).trim();
		return "";
	}

	public static String formatAiReviewDetails(String content)
	{
		String source = defaultIfBlank(content, "");
		if(source.isBlank())
			return "";

		source = source.replace("\\r", "").replace("\\\\n", "\\n").replace("**", "").replace("__", "");
		Matcher detailsMatcher = Pattern.compile("(?is)\\bDETAILS\\b\\s*[:=]\\s*(.+)$").matcher(source);
		if(detailsMatcher.find())
			source = detailsMatcher.group(1).trim();
		String[] lines = source.split("\\n");

		ArrayList<String> points = new ArrayList<String>();
		for(String rawLine : lines)
		{
			if(rawLine == null)
				continue;
			String line = rawLine.trim();
			if(line.isBlank())
				continue;

			line = line.replaceFirst("(?i)^\\s*(RESULT|VERDICT|DECISION|OUTCOME)\\s*[:=]\\s*(PASS|FAIL|FAILED|APPROVED|REJECTED)\\s*", "").trim();
			line = line.replaceFirst("(?i)^\\s*SUMMARY\\s*[:=]\\s*", "").trim();
			line = line.replaceFirst("(?i)^\\s*DETAILS\\s*[:=]\\s*", "").trim();
			if(line.isBlank())
				continue;

			ArrayList<String> fragments = splitInlineNumberedPoints(line);
			for(String fragment : fragments)
			{
				String item = defaultIfBlank(fragment, "").trim();
				if(item.isBlank())
					continue;

				item = item.replaceFirst("^\\s*[-*]\\s+", "");
				item = item.replaceFirst("^\\s*\\d+[.)]\\s+", "");
				item = item.trim();
				if(item.isBlank())
					continue;
				points.add(item);
			}
		}

		if(points.isEmpty())
		{
			String flattened = source.replaceAll("(?i)\\b(RESULT|SUMMARY|DETAILS|VERDICT|DECISION|OUTCOME)\\s*[:=]", "");
			flattened = flattened.replace("\\n", " ").replaceAll("\\s+", " ").trim();
			if(!flattened.isBlank())
				points.add(flattened);
		}

		if(points.isEmpty())
			return "";

		StringBuilder formatted = new StringBuilder();
		int limit = Math.min(points.size(), 10);
		for(int i = 0; i < limit; i++)
		{
			formatted.append(i + 1).append(". ").append(points.get(i));
			if(i < limit - 1)
				formatted.append("\\n");
		}
		return formatted.toString();
	}

	public static ArrayList<String> splitInlineNumberedPoints(String line)
	{
		ArrayList<String> parts = new ArrayList<String>();
		String source = defaultIfBlank(line, "").trim();
		if(source.isBlank())
			return parts;

		Matcher matcher = Pattern.compile("(?:(?<=^)|(?<=\\s))(\\d+[.)])\\s+").matcher(source);
		ArrayList<Integer> starts = new ArrayList<Integer>();
		while(matcher.find())
			starts.add(matcher.start(1));

		if(starts.size() >= 2 || (starts.size() == 1 && starts.get(0) == 0))
		{
			for(int i = 0; i < starts.size(); i++)
			{
				int start = starts.get(i);
				int end = (i + 1 < starts.size()) ? starts.get(i + 1) : source.length();
				String part = source.substring(start, end).trim();
				if(!part.isBlank())
					parts.add(part);
			}
		}

		if(parts.isEmpty())
			parts.add(source);
		return parts;
	}

	public static Boolean parseAiPassFail(String content, String rawBody)
	{
		String source = defaultIfBlank(content, "");
		if(source.isBlank())
			source = defaultIfBlank(rawBody, "");
		source = source.replace("**", "").replace("__", "");

		String patterns = "(PASS|FAIL|FAILED|APPROVED|REJECTED)";
		Matcher labeled = Pattern.compile("(?im)\\b(RESULT|VERDICT|DECISION|OUTCOME)\\b\\s*(?:[:=]|-|->|=>)\\s*" + patterns + "\\b").matcher(source);
		if(labeled.find())
			return mapDecisionToken(labeled.group(2));

		Matcher markdownList = Pattern.compile("(?im)^\\s*[-*]\\s*(RESULT|VERDICT|DECISION|OUTCOME)\\s*(?:[:=]|-|->|=>)\\s*" + patterns + "\\b").matcher(source);
		if(markdownList.find())
			return mapDecisionToken(markdownList.group(2));

		Matcher jsonResult = Pattern.compile("(?im)\"(result|verdict|decision|outcome)\"\\s*:\\s*\"" + patterns + "\"").matcher(source);
		if(jsonResult.find())
			return mapDecisionToken(jsonResult.group(2));

		Matcher standalone = Pattern.compile("(?im)^\\s*" + patterns + "\\s*$").matcher(source);
		if(standalone.find())
			return mapDecisionToken(standalone.group(1));

		return null;
	}

	public static Boolean mapDecisionToken(String token)
	{
		String normalized = defaultIfBlank(token, "").trim().toUpperCase();
		if("PASS".equals(normalized) || "APPROVED".equals(normalized))
			return true;
		if("FAIL".equals(normalized) || "FAILED".equals(normalized) || "REJECTED".equals(normalized))
			return false;
		return null;
	}

	public static String jsonUnescape(String raw)
	{
		if(raw == null)
			return "";
		return raw.replace("\\\\n", "\\n").replace("\\\\r", "").replace("\\\\\"", "\"").replace("\\\\\\\\", "\\");
	}

	public static String trimTo(String value, int maxLen)
	{
		if(value == null)
			return "";
		if(value.length() <= maxLen)
			return value;
		return value.substring(0, maxLen) + "\\n\\n[truncated]";
	}

	public static String buildAiFailureMessage(String prNumber, String pullRequestUrl, String summary, String details)
	{
		StringBuilder msg = new StringBuilder();
		msg.append("### AI Review Report\n\n");
		msg.append("PR #").append(defaultIfBlank(prNumber, "")).append(" ");
		if(pullRequestUrl != null && !pullRequestUrl.isBlank())
			msg.append("(").append(pullRequestUrl).append(")");
		msg.append("\n\n");
		msg.append("**Summary:** ").append(defaultIfBlank(summary, "AI review report")).append("\n\n");
		msg.append("**Details (Step-by-step):**\n").append(trimTo(defaultIfBlank(details, "No details provided."), 3000)).append("\n\n");
		msg.append("Please fix the blocking issues and push new changes to rerun AI review.");
		return msg.toString();
	}

	public static void postPullRequestComment(String repository, String prNumber, String githubToken, String commentBody)
	{
		try
		{
			String payload = "{\"body\":\"" + jsonEscape(commentBody) + "\"}";
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Accept", "application/vnd.github+json");
			headers.put("Authorization", "Bearer " + githubToken);
			headers.put("Content-Type", "application/json");
			HttpResult response = sendHttpRequest("POST", "https://api.github.com/repos/" + repository + "/issues/" + prNumber + "/comments", payload, headers);
			if(response.status < 200 || response.status > 299)
				System.err.println("Failed to post AI review PR comment: status=" + response.status + ", body=" + preview(response.body));
		}
		catch(Exception e)
		{
			System.err.println("Failed to post AI review PR comment: " + e.getMessage());
		}
	}

	public static void postAiFailureToCliqThread(String cliqEndpoint, String cliqThreadId, String imageUrl, String failureMessage)
	{
		if(cliqEndpoint == null || cliqEndpoint.isBlank())
			return;
		try
		{
			String message = "AI Review Report.\\n" + failureMessage;
			if(cliqThreadId != null && !cliqThreadId.isBlank())
			{
				ArrayList<String> candidates = buildReplyToCandidates(cliqThreadId);
				for(String candidate : candidates)
				{
					HttpResult result = postJson(cliqEndpoint, buildCliqCardPayload(message, imageUrl, candidate));
					if(result.status >= 200 && result.status <= 299)
						return;
				}
			}
			postJson(cliqEndpoint, buildCliqCardPayload(message, imageUrl, null));
		}
		catch(Exception e)
		{
			System.err.println("Failed to post AI review failure in Cliq: " + e.getMessage());
		}
	}

	public static void setAiReviewCheckRun(String repository, String headSha, String githubToken, String checkName, boolean passed, String summary, String details)
	{
		try
		{
			String conclusion = passed ? "success" : "failure";
			String payload = "{"
				+ "\"name\":\"" + jsonEscape(checkName) + "\"," 
				+ "\"head_sha\":\"" + jsonEscape(headSha) + "\"," 
				+ "\"status\":\"completed\"," 
				+ "\"conclusion\":\"" + conclusion + "\"," 
				+ "\"output\":{\"title\":\"" + jsonEscape(checkName) + "\",\"summary\":\"" + jsonEscape(defaultIfBlank(summary, "AI review completed")) + "\",\"text\":\"" + jsonEscape(trimTo(defaultIfBlank(details, ""), 5000)) + "\"}"
				+ "}";

			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Accept", "application/vnd.github+json");
			headers.put("Authorization", "Bearer " + githubToken);
			headers.put("Content-Type", "application/json");
			HttpResult response = sendHttpRequest("POST", "https://api.github.com/repos/" + repository + "/check-runs", payload, headers);
			if(response.status < 200 || response.status > 299)
				System.err.println("Failed to set AI review check run: status=" + response.status + ", body=" + preview(response.body));
		}
		catch(Exception e)
		{
			System.err.println("Failed to set AI review check run: " + e.getMessage());
		}
	}

	public static boolean isTrue(String value)
	{
		if(value == null)
			return false;
		String normalized = value.trim().toLowerCase();
		return "true".equals(normalized) || "1".equals(normalized) || "yes".equals(normalized);
	}

	public static String defaultIfBlank(String value, String fallback)
	{
		if(value == null || value.isBlank())
			return fallback;
		return value;
	}

	public static void debug(String message)
	{
		System.out.println("[CliqInformerDebug] " + message);
	}

	public static String preview(String value)
	{
		if(value == null)
			return "<null>";
		String sanitized = value.replace("\n", " ").replace("\r", " ").trim();
		if(sanitized.length() > 280)
			return sanitized.substring(0, 280) + "...";
		return sanitized;
	}
}
