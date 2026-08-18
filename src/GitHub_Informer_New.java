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
			boolean useCliqBotAuth = isCliqBotAuthEndpoint(CliqChannelLink);
			if(isCliqWebhookEndpoint(CliqChannelLink) || useCliqBotAuth)
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
				String projectTokenRaw = (String) System.getenv("PROJECT_TOKEN");
				String pullRequestTitleRaw = (String) System.getenv("PULL_REQUEST_TITLE");
				String pullRequestBodyRaw = (String) System.getenv("PULL_REQUEST_BODY");
				String pullRequestUrlRaw = (String) System.getenv("PULL_REQUEST_URL");
				String pullRequestDiffUrlRaw = (String) System.getenv("PULL_REQUEST_DIFF_URL");
				String pullRequestBaseShaRaw = (String) System.getenv("PULL_REQUEST_BASE_SHA");
				String pullRequestHeadShaRaw = (String) System.getenv("PULL_REQUEST_HEAD_SHA");
				String pullRequestBeforeShaRaw = (String) System.getenv("PULL_REQUEST_BEFORE_SHA");
				String prLabelsRaw = (String) System.getenv("PR_LABELS");
				// Previous storage mode was PR marker comments ("comment").
				// Keep default as comment so existing behavior remains backward-compatible.
				String threadStorageMode = defaultIfBlank((String) System.getenv("CLIQ_THREAD_STORAGE_MODE"), "comment").trim().toLowerCase();
				String projectOwnerRaw = defaultIfBlank((String) System.getenv("GITHUB_PROJECT_OWNER"), "");
				String projectNumberRaw = defaultIfBlank((String) System.getenv("GITHUB_PROJECT_NUMBER"), "");
				String projectIdRaw = defaultIfBlank((String) System.getenv("GITHUB_PROJECT_ID"), "");
				String projectThreadFieldIdRaw = defaultIfBlank((String) System.getenv("GITHUB_PROJECT_THREAD_FIELD_ID"), "");
				String projectThreadFieldNameRaw = defaultIfBlank((String) System.getenv("GITHUB_PROJECT_THREAD_FIELD_NAME"), "Cliq Thread ID");
				String storageToken = githubToken;
				if("project".equals(threadStorageMode))
				{
					storageToken = defaultIfBlank(projectTokenRaw, githubToken);
				}
				debug("EventNameRaw=" + eventNameRaw + ", ActionRaw=" + ActionRaw + ", isPrEvent=" + isPrEvent + ", isPullRequestCommentEvent=" + isPullRequestCommentEvent + ", isPullRequestReviewEvent=" + isPullRequestReviewEvent + ", isPullRequestReviewCommentEvent=" + isPullRequestReviewCommentEvent + ", prNumber=" + prNumber + ", hasGithubToken=" + (githubToken != null && !githubToken.isBlank()) + ", hasProjectToken=" + (projectTokenRaw != null && !projectTokenRaw.isBlank()) + ", storageMode=" + threadStorageMode + ", hasStorageToken=" + (storageToken != null && !storageToken.isBlank()));
				String prThreadId = null;
				if(isPrEvent && prNumber != null && !prNumber.isBlank() && storageToken != null && !storageToken.isBlank())
				{
				  prThreadId = fetchCliqThreadId(
					  Repository,
					  prNumber,
					  storageToken,
					  threadStorageMode,
					  projectOwnerRaw,
					  projectNumberRaw,
					  projectIdRaw,
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
						HttpResult threadedResult = postJson(CliqChannelLink, buildCliqPayload(msg, GitHubInformerURL, threadMessageIdCandidate, useCliqBotAuth));
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
					HttpResult directResult = postJson(CliqChannelLink, buildCliqPayload(msg, GitHubInformerURL, null, useCliqBotAuth));
					status = directResult.status;
					localResponse = directResult.body;
					debug("Cliq post status=" + status + ", usingReplyTo=false, responsePreview=" + preview(localResponse));
					responseContent.append(localResponse);      
				  }

				  // Fallback: if all threaded attempts fail, retry as normal channel message.
				  if(!postedInThread && prThreadId != null && !prThreadId.isBlank())
				  {
					HttpResult fallbackResult = postJson(CliqChannelLink, buildCliqPayload(msg, GitHubInformerURL, null, useCliqBotAuth));
					status = fallbackResult.status;
					localResponse = fallbackResult.body;
					debug("Fallback normal post status=" + status + ", responsePreview=" + preview(localResponse));
					responseContent.append(localResponse);
				  }

				  if(isPrEvent && (prThreadId == null || prThreadId.isBlank()) && (createdThreadId == null || createdThreadId.isBlank()))
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

				if(isPrEvent && (prThreadId == null || prThreadId.isBlank()) && createdThreadId != null && !createdThreadId.isBlank() && prNumber != null && !prNumber.isBlank() && storageToken != null && !storageToken.isBlank())
				{
				  ThreadStorageResult storageResult = upsertCliqThreadIdWithResult(
					  Repository,
					  prNumber,
					  storageToken,
					  createdThreadId,
					  threadStorageMode,
					  projectOwnerRaw,
					  projectNumberRaw,
					  projectIdRaw,
					  projectThreadFieldIdRaw,
					  projectThreadFieldNameRaw
				  );
				  boolean threadSaved = storageResult.saved;

				  if("project".equalsIgnoreCase(defaultIfBlank(threadStorageMode, "comment")) && !storageResult.savedInProject)
				  {
					String failureReason = defaultIfBlank(storageResult.projectFailureReason, "Project custom field update failed for an unknown reason.");
					String warningMessage = "### Cliq Thread Storage Warning\n\n"
						+ "GitHub Informer could not store the Cliq thread id in the configured Project custom field.\n\n"
						+ "**Reason:** " + failureReason + "\n\n"
						+ "Please verify the custom field name and project identifier in your workflow YAML, then rerun.";
					if(githubToken != null && !githubToken.isBlank())
						postPullRequestComment(Repository, prNumber, githubToken, warningMessage);
				  }

				  if(!threadSaved)
				  {
					System.err.println("PR thread id was not saved in project custom field. Check project field configuration and token scope.");
				  }
				}
				else if(isPrEvent && (prThreadId == null || prThreadId.isBlank()) && (createdThreadId == null || createdThreadId.isBlank()))
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
						pullRequestBeforeShaRaw,
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
			  ERROR_MESSAGE = "Invalid Endpoint. Endpoint must be either <Zoho Cliq Channel API Endpoint>?zapikey=<Zoho Cliq Webhook Token> or https://cliq.zoho.com/api/v2/channelsbyname/<CHANNEL_UNIQUE_NAME>/message?bot_unique_name=<BOT_UNIQUE_NAME>&zapikey=<Zoho Cliq Webhook Token>.";
			else if(GITHUB_ERROR)
			  ERROR_MESSAGE = "Environmental Variable GITHUB_OUTPUT missing";
			else if(MESSAGE_SEND_FAILURE_ERROR)
			  ERROR_MESSAGE = responseContent.toString().isBlank() ? ERROR_MESSAGE : responseContent.toString();
			else if(status == 204 || status == 200 || status == 201)
			  ERROR_MESSAGE = "GitHub Informer executed Successfully";
			writeGithubOutput(status,ERROR_MESSAGE);
		}  catch (MalformedURLException e) {
			ERROR_MESSAGE = "Invalid Endpoint URL. Please provide channel-endpoint as either <Cliq Channel API Endpoint>?zapikey=<Cliq Webhook Token> or /channelsbyname/<CHANNEL_UNIQUE_NAME>/message?bot_unique_name=<BOT_UNIQUE_NAME>&zapikey=<Cliq Webhook Token>.";
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

	public static boolean isCliqWebhookEndpoint(String endpoint)
	{
		String value = defaultIfBlank(endpoint, "");
		return value.contains("message") && value.contains("https://cliq.zoho") && value.contains("/api/v2/") && value.contains("?zapikey=");
	}

	public static boolean isCliqBotAuthEndpoint(String endpoint)
	{
		String value = defaultIfBlank(endpoint, "");
		return value.contains("https://cliq.zoho") && value.contains("/api/v2/channelsbyname/") && value.contains("/message") && value.contains("bot_unique_name=") && value.contains("zapikey=");
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

	public static String buildCliqPayload(String message, String imageUrl, String threadMessageId, boolean useCliqBotAuth)
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
		if(!useCliqBotAuth)
			payload.append("\n\"bot\":\n{\n\"name\":\"GitHub Informer for Zoho Cliq\",\n\"image\":\"").append(jsonEscape(imageUrl)).append("\"}}\n");
		else
			payload.append("\n}\n");
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

	public static String fetchCliqThreadId(String repository, String prNumber, String githubToken, String storageMode, String projectOwner, String projectNumberRaw, String projectIdRaw, String projectThreadFieldId, String projectThreadFieldName)
	{
		if("project".equalsIgnoreCase(defaultIfBlank(storageMode, "comment")))
		{
			String projectThreadId = fetchCliqThreadIdFromProjectField(repository, prNumber, githubToken, projectOwner, projectNumberRaw, projectIdRaw, projectThreadFieldName);
			if(projectThreadId != null && !projectThreadId.isBlank())
				return projectThreadId;
			debug("Project field storage did not return thread id. Project-only mode: marker comment fallback disabled.");
			return null;
		}
		return fetchCliqThreadIdFromPRComments(repository, prNumber, githubToken);
	}

	public static class ThreadStorageResult
	{
		public boolean saved;
		public boolean savedInProject;
		public boolean savedInFallback;
		public String projectFailureReason;

		public ThreadStorageResult(boolean saved, boolean savedInProject, boolean savedInFallback, String projectFailureReason)
		{
			this.saved = saved;
			this.savedInProject = savedInProject;
			this.savedInFallback = savedInFallback;
			this.projectFailureReason = defaultIfBlank(projectFailureReason, "");
		}
	}

	public static boolean upsertCliqThreadId(String repository, String prNumber, String githubToken, String threadId, String storageMode, String projectOwner, String projectNumberRaw, String projectIdRaw, String projectThreadFieldId, String projectThreadFieldName)
	{
		ThreadStorageResult result = upsertCliqThreadIdWithResult(repository, prNumber, githubToken, threadId, storageMode, projectOwner, projectNumberRaw, projectIdRaw, projectThreadFieldId, projectThreadFieldName);
		return result.saved;
	}

	public static ThreadStorageResult upsertCliqThreadIdWithResult(String repository, String prNumber, String githubToken, String threadId, String storageMode, String projectOwner, String projectNumberRaw, String projectIdRaw, String projectThreadFieldId, String projectThreadFieldName)
	{
		if("project".equalsIgnoreCase(defaultIfBlank(storageMode, "comment")))
		{
			StringBuilder projectFailureReason = new StringBuilder();
			boolean savedInProject = upsertCliqThreadIdInProjectField(repository, prNumber, githubToken, threadId, projectOwner, projectNumberRaw, projectIdRaw, projectThreadFieldId, projectThreadFieldName, projectFailureReason);
			if(savedInProject)
				return new ThreadStorageResult(true, true, false, "");
			debug("Project field write failed. Project-only mode: marker comment fallback disabled.");
			return new ThreadStorageResult(false, false, false, projectFailureReason.toString());
		}
		boolean markerSaved = upsertCliqThreadIdComment(repository, prNumber, githubToken, threadId);
		return new ThreadStorageResult(markerSaved, false, markerSaved, "");
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

	public static String fetchCliqThreadIdFromProjectField(String repository, String prNumber, String githubToken, String projectOwner, String projectNumberRaw, String projectIdRaw, String projectThreadFieldName)
	{
		ProjectItemContext context = resolveProjectItemContext(repository, prNumber, githubToken, projectOwner, projectNumberRaw, projectIdRaw, projectThreadFieldName);
		if(context == null)
			return null;
		String value = defaultIfBlank(context.fieldValue, "").trim();
		if(value.isBlank())
			return null;
		debug("Found Cliq thread id in project field storage.");
		return value;
	}

	public static boolean upsertCliqThreadIdInProjectField(String repository, String prNumber, String githubToken, String threadId, String projectOwner, String projectNumberRaw, String projectIdRaw, String projectThreadFieldId, String projectThreadFieldName)
	{
		return upsertCliqThreadIdInProjectField(repository, prNumber, githubToken, threadId, projectOwner, projectNumberRaw, projectIdRaw, projectThreadFieldId, projectThreadFieldName, null);
	}

	public static boolean upsertCliqThreadIdInProjectField(String repository, String prNumber, String githubToken, String threadId, String projectOwner, String projectNumberRaw, String projectIdRaw, String projectThreadFieldId, String projectThreadFieldName, StringBuilder failureReasonOut)
	{
		try
		{
			ProjectItemContext context = resolveProjectItemContext(repository, prNumber, githubToken, projectOwner, projectNumberRaw, projectIdRaw, projectThreadFieldName);
			if(context == null || context.projectId == null || context.projectId.isBlank() || context.itemId == null || context.itemId.isBlank())
			{
				String reason = "Project thread storage skipped: unable to resolve project/item context.";
				if(failureReasonOut != null)
					failureReasonOut.append(reason);
				System.err.println(reason);
				return false;
			}

			String fieldId = defaultIfBlank(projectThreadFieldId, "").trim();
			if(fieldId.isBlank())
			{
				fieldId = resolveProjectFieldIdByName(githubToken, context.projectId, projectThreadFieldName);
			}
			else if(!fieldId.startsWith("PVTF_") && !fieldId.startsWith("PVTSSF_") && !fieldId.startsWith("PVTIF_"))
			{
				fieldId = resolveProjectFieldIdByIdentifier(githubToken, context.projectId, fieldId, projectThreadFieldName);
			}
			if(fieldId == null || fieldId.isBlank())
			{
				String reason = "Project thread storage skipped: unable to resolve project field id.";
				if(failureReasonOut != null)
					failureReasonOut.append(reason);
				System.err.println(reason);
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
			String reason = "Unable to write Cliq thread id to project field: status=" + response.status + ", body=" + preview(response.body);
			if(failureReasonOut != null)
				failureReasonOut.append(reason);
			System.err.println(reason);
		}
		catch(Exception e)
		{
			String reason = "Unable to write Cliq thread id to project field: " + e.getMessage();
			if(failureReasonOut != null)
				failureReasonOut.append(reason);
			System.err.println(reason);
		}
		return false;
	}

	public static ProjectItemContext resolveProjectItemContext(String repository, String prNumberRaw, String githubToken, String projectOwnerRaw, String projectNumberRaw, String projectIdRaw, String projectThreadFieldNameRaw)
	{
		try
		{
			String owner = defaultIfBlank(projectOwnerRaw, "").trim();
			String projectNumberText = defaultIfBlank(projectNumberRaw, "").trim();
			String configuredProjectId = defaultIfBlank(projectIdRaw, "").trim();
			String fieldName = defaultIfBlank(projectThreadFieldNameRaw, "Cliq Thread ID").trim();
			if(configuredProjectId.isBlank() && (owner.isBlank() || projectNumberText.isBlank()))
			{
				debug("Project storage is not configured: set GITHUB_PROJECT_ID or both GITHUB_PROJECT_OWNER and GITHUB_PROJECT_NUMBER.");
				return null;
			}

			int prNumber;
			int projectNumber = -1;
			try
			{
				prNumber = Integer.parseInt(defaultIfBlank(prNumberRaw, "").trim());
				if(!projectNumberText.isBlank())
					projectNumber = Integer.parseInt(projectNumberText);
			}
			catch(Exception e)
			{
				System.err.println("Project storage parse error: invalid project or PR number.");
				return null;
			}

			String[] repoParts = defaultIfBlank(repository, "").split("/");
			if(repoParts.length != 2)
				return null;

			String projectId = configuredProjectId;
			if(projectId == null || projectId.isBlank())
			{
				projectId = resolveProjectIdByOwnerAndNumber(githubToken, owner, projectNumber);
			}
			if(projectId == null || projectId.isBlank())
			{
				System.err.println("Project thread storage skipped: unable to resolve project id.");
				return null;
			}

			String pullRequestNodeId = resolvePullRequestNodeId(githubToken, repoParts[0], repoParts[1], prNumber);
			if(pullRequestNodeId == null || pullRequestNodeId.isBlank())
			{
				System.err.println("Project thread storage skipped: unable to resolve pull request node id.");
				return null;
			}

			ProjectItemContext existingContext = resolveProjectItemContextFromPullRequestNode(githubToken, pullRequestNodeId, owner, projectNumber, projectId, fieldName);
			if(existingContext != null && existingContext.itemId != null && !existingContext.itemId.isBlank())
				return existingContext;

			String addedItemId = addPullRequestToProject(githubToken, projectId, pullRequestNodeId);
			if(addedItemId != null && !addedItemId.isBlank())
				return new ProjectItemContext(addedItemId, projectId, "");

			// One more lookup in case PR was already added concurrently.
			ProjectItemContext contextAfterAdd = resolveProjectItemContextFromPullRequestNode(githubToken, pullRequestNodeId, owner, projectNumber, projectId, fieldName);
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
			String userProjectId = resolveProjectIdFromUser(githubToken, owner, projectNumber);
			if(userProjectId != null && !userProjectId.isBlank())
				return userProjectId;

			String orgProjectId = resolveProjectIdFromOrganization(githubToken, owner, projectNumber);
			if(orgProjectId != null && !orgProjectId.isBlank())
				return orgProjectId;
		}
		catch(Exception e)
		{
			System.err.println("Unable to resolve project id: " + e.getMessage());
		}
		return "";
	}

	public static String resolveProjectFieldIdByIdentifier(String githubToken, String projectId, String fieldIdentifierRaw, String fieldNameRaw)
	{
		try
		{
			String fieldIdentifier = defaultIfBlank(fieldIdentifierRaw, "").trim();
			String fieldName = defaultIfBlank(fieldNameRaw, "Cliq Thread ID").trim();
			String query = "query($projectId:ID!){node(id:$projectId){... on ProjectV2{fields(first:100){nodes{... on ProjectV2FieldCommon{id name} ... on ProjectV2Field{databaseId} ... on ProjectV2SingleSelectField{databaseId} ... on ProjectV2IterationField{databaseId}}}}}}";
			String payload = "{"
				+ "\"query\":\"" + jsonEscape(query) + "\"," 
				+ "\"variables\":{\"projectId\":\"" + jsonEscape(projectId) + "\"}}";
			HttpResult response = postGitHubGraphql(githubToken, payload);
			if(response.status < 200 || response.status > 299 || response.body == null || response.body.isBlank() || response.body.contains("\"errors\""))
				return "";

			Matcher matcher = Pattern.compile("\\\"id\\\":\\\"([^\\\"]+)\\\",\\\"name\\\":\\\"((?:\\\\.|[^\\\\\"])*)\\\"").matcher(response.body);
			ArrayList<String> availableFields = new ArrayList<String>();
			String fallbackByName = "";
			while(matcher.find())
			{
				String id = matcher.group(1);
				String name = jsonUnescape(defaultIfBlank(matcher.group(2), ""));
				if(name != null && !name.isBlank())
					availableFields.add(name + " [" + id + "]");
				if(fieldIdentifier.equalsIgnoreCase(id))
					return id;
				if(fieldName.equalsIgnoreCase(defaultIfBlank(name, "").trim()) && fallbackByName.isBlank())
					fallbackByName = id;

				int windowStart = Math.max(0, matcher.start() - 160);
				int windowEnd = Math.min(response.body.length(), matcher.end() + 220);
				String window = response.body.substring(windowStart, windowEnd);
				Matcher dbMatcher = Pattern.compile("\\\"databaseId\\\":(\\d+)").matcher(window);
				if(dbMatcher.find() && fieldIdentifier.equals(defaultIfBlank(dbMatcher.group(1), "").trim()))
					return id;
			}

			if(!fallbackByName.isBlank())
			{
				debug("Project field identifier not found. Falling back to name='" + fieldName + "'.");
				return fallbackByName;
			}
			debug("Project field identifier not found. Requested='" + fieldIdentifier + "', available=" + availableFields.toString());
		}
		catch(Exception e)
		{
			System.err.println("Unable to resolve project field id by identifier: " + e.getMessage());
		}
		return "";
	}

	public static String resolveProjectIdFromUser(String githubToken, String owner, int projectNumber)
	{
		try
		{
			String query = "query($owner:String!,$projectNumber:Int!){user(login:$owner){projectV2(number:$projectNumber){id}}}";
			String payload = "{"
				+ "\"query\":\"" + jsonEscape(query) + "\","
				+ "\"variables\":{"
				+ "\"owner\":\"" + jsonEscape(owner) + "\","
				+ "\"projectNumber\":" + projectNumber
				+ "}}";
			HttpResult response = postGitHubGraphql(githubToken, payload);
			if(response.status < 200 || response.status > 299 || response.body == null || response.body.isBlank())
				return "";
			Matcher matcher = Pattern.compile("\\\"projectV2\\\":\\{\\\"id\\\":\\\"([^\\\"]+)\\\"\\}").matcher(response.body);
			if(matcher.find())
				return matcher.group(1);
		}
		catch(Exception e)
		{
			System.err.println("Unable to resolve user project id: " + e.getMessage());
		}
		return "";
	}

	public static String resolveProjectIdFromOrganization(String githubToken, String owner, int projectNumber)
	{
		try
		{
			String query = "query($owner:String!,$projectNumber:Int!){organization(login:$owner){projectV2(number:$projectNumber){id}}}";
			String payload = "{"
				+ "\"query\":\"" + jsonEscape(query) + "\","
				+ "\"variables\":{"
				+ "\"owner\":\"" + jsonEscape(owner) + "\","
				+ "\"projectNumber\":" + projectNumber
				+ "}}";
			HttpResult response = postGitHubGraphql(githubToken, payload);
			if(response.status < 200 || response.status > 299 || response.body == null || response.body.isBlank())
				return "";
			Matcher matcher = Pattern.compile("\\\"projectV2\\\":\\{\\\"id\\\":\\\"([^\\\"]+)\\\"\\}").matcher(response.body);
			if(matcher.find())
				return matcher.group(1);
		}
		catch(Exception e)
		{
			System.err.println("Unable to resolve organization project id: " + e.getMessage());
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

	public static ProjectItemContext resolveProjectItemContextFromPullRequestNode(String githubToken, String pullRequestNodeId, String owner, int projectNumber, String configuredProjectId, String fieldName)
	{
		try
		{
			String query = "query($prId:ID!,$fieldName:String!){node(id:$prId){... on PullRequest{projectItems(first:100){nodes{id project{id number} fieldValueByName(name:$fieldName){... on ProjectV2ItemFieldTextValue{text}}}}}}}";
			String payload = "{"
				+ "\"query\":\"" + jsonEscape(query) + "\","
				+ "\"variables\":{"
				+ "\"prId\":\"" + jsonEscape(pullRequestNodeId) + "\","
				+ "\"fieldName\":\"" + jsonEscape(fieldName) + "\""
				+ "}}";
			HttpResult response = postGitHubGraphql(githubToken, payload);
			if(response.status < 200 || response.status > 299 || response.body == null || response.body.isBlank() || response.body.contains("\"errors\""))
				return null;

			Matcher nodeMatcher = Pattern.compile("\\{\\\"id\\\":\\\"([^\\\"]+)\\\",\\\"project\\\":\\{\\\"id\\\":\\\"([^\\\"]+)\\\",\\\"number\\\":(\\d+)\\},\\\"fieldValueByName\\\":(null|\\{\\\"text\\\":\\\"((?:\\\\.|[^\\\\\"])*)\\\"[^\\}]*\\})\\}", Pattern.DOTALL).matcher(response.body);
			while(nodeMatcher.find())
			{
				String itemId = nodeMatcher.group(1);
				String currentProjectId = nodeMatcher.group(2);
				int currentProjectNumber;
				try
				{
					currentProjectNumber = Integer.parseInt(nodeMatcher.group(3));
				}
				catch(Exception e)
				{
					continue;
				}
				if(!defaultIfBlank(configuredProjectId, "").isBlank())
				{
					if(!configuredProjectId.equals(currentProjectId))
						continue;
				}
				else if(currentProjectNumber != projectNumber)
					continue;
				String value = "";
				if(nodeMatcher.group(4) != null && !"null".equals(nodeMatcher.group(4)))
					value = jsonUnescape(defaultIfBlank(nodeMatcher.group(5), ""));
				return new ProjectItemContext(itemId, currentProjectId, value);
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
			ArrayList<String> availableFields = new ArrayList<String>();
			while(matcher.find())
			{
				String id = matcher.group(1);
				String name = jsonUnescape(defaultIfBlank(matcher.group(2), ""));
				if(name != null && !name.isBlank())
					availableFields.add(name + " [" + id + "]");
				// ProjectV2 field ids are not always prefixed consistently across field types.
				if(fieldName.equalsIgnoreCase(defaultIfBlank(name, "").trim()))
					return id;
			}
			debug("Project field name not found. Requested='" + fieldName + "', available=" + availableFields.toString());
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
		public String conclusion;
		public String summary;
		public String details;

		public AiReviewDecision(boolean passed, String summary, String details)
		{
			this(passed, passed ? "success" : "failure", summary, details);
		}

		public AiReviewDecision(boolean passed, String conclusion, String summary, String details)
		{
			this.passed = passed;
			this.conclusion = defaultIfBlank(conclusion, passed ? "success" : "failure");
			this.summary = summary == null ? "" : summary;
			this.details = details == null ? "" : details;
		}
	}

	public static void handleAiReviewGate(String repository, String prNumber, String eventNameRaw, String actionRaw, String prLabelsRaw, String pullRequestTitle, String pullRequestBody, String pullRequestUrl, String pullRequestDiffUrl, String pullRequestBaseSha, String pullRequestHeadSha, String pullRequestBeforeSha, String githubToken, String cliqEndpoint, String cliqThreadId, String imageUrl)
	{
		if(!isTrue(System.getenv("AI_REVIEW_ENABLED")))
			return;
		if(prNumber == null || prNumber.isBlank())
			return;

		String triggerMode = defaultIfBlank(System.getenv("AI_REVIEW_TRIGGER"), "auto").trim().toLowerCase();
		boolean runOnSync = isTrue(defaultIfBlank(System.getenv("AI_REVIEW_ON_SYNC"), "true"));
		boolean incrementalOnSync = isTrue(defaultIfBlank(System.getenv("AI_REVIEW_INCREMENTAL_ON_SYNC"), "true"));
		String triggerLabel = defaultIfBlank(System.getenv("AI_REVIEW_LABEL"), "");
		String fullReviewLabel = defaultIfBlank(System.getenv("AI_REVIEW_FULL_LABEL"), "").trim();

		if(!shouldRunAiReviewForEvent(triggerMode, triggerLabel, runOnSync, eventNameRaw, actionRaw, prLabelsRaw, fullReviewLabel))
			return;

		// The "ready for merge" safety-net gate: when this run was triggered specifically by
		// applying the full-review label, always do a full base...head review regardless of
		// incremental settings, so any issue introduced earlier in the PR (and not re-flagged
		// by incremental per-push reviews) is still caught once before merge.
		boolean isFullReviewLabelEvent = "labeled".equals(actionRaw) && !fullReviewLabel.isBlank()
			&& hasLabel(prLabelsRaw, fullReviewLabel);

		// Only treat this as an incremental (delta-only) review when it is genuinely a
		// "synchronize" push to an already-open PR AND we have a usable previous head SHA.
		// GitHub sends a real commit SHA in event.before for a normal fast-forward push, but
		// sends the all-zero SHA (or omits it) for the very first synchronize after certain
		// edge cases (e.g. base branch changed) - in those cases there is nothing sensible to
		// diff against incrementally, so we deliberately fall back to the full base...head diff.
		boolean isSynchronizeEvent = "synchronize".equals(actionRaw);
		boolean hasUsablePreviousHead = pullRequestBeforeSha != null && !pullRequestBeforeSha.isBlank()
			&& !pullRequestBeforeSha.matches("0+");
		String incrementalBaseSha = (incrementalOnSync && isSynchronizeEvent && hasUsablePreviousHead && !isFullReviewLabelEvent)
			? pullRequestBeforeSha
			: null;

		String checkName = defaultIfBlank(System.getenv("AI_REVIEW_CHECK_NAME"), "AI Review Gate");
		if(isFullReviewLabelEvent)
			debug("AI Review Gate: '" + fullReviewLabel + "' label applied - running full base...head review as pre-merge safety net.");
		AiReviewDecision decision = evaluateAiReviewDecision(repository, prNumber, pullRequestTitle, pullRequestBody, pullRequestUrl, pullRequestDiffUrl, pullRequestBaseSha, pullRequestHeadSha, incrementalBaseSha, githubToken);

		if(githubToken != null && !githubToken.isBlank() && pullRequestHeadSha != null && !pullRequestHeadSha.isBlank())
		{
			setAiReviewCheckRun(repository, pullRequestHeadSha, githubToken, checkName, decision.conclusion, decision.summary, decision.details);
		}
		else
		{
			System.err.println("AI review check run skipped: missing github token or PR head sha.");
		}

		if(!decision.passed)
		{
			String failureMessage = buildAiFailureMessage(prNumber, pullRequestUrl, decision.summary, decision.details);
			boolean prCommentPosted = false;
			if(githubToken != null && !githubToken.isBlank())
			{
				postPullRequestComment(repository, prNumber, githubToken, failureMessage);
				prCommentPosted = true;
			}
			else
			{
				System.err.println("AI review failure PR comment skipped: missing github token.");
			}

			int cliqSummaryLength = parseIntOrDefault(System.getenv("AI_REVIEW_CLIQ_SUMMARY_LENGTH"), 300);
			String actionsRunUrl = buildActionsRunUrl(repository);
			String cliqNotification = buildAiFailureCliqNotification(prNumber, pullRequestUrl, decision.summary, prCommentPosted, cliqSummaryLength, actionsRunUrl);
			postAiFailureToCliqThread(cliqEndpoint, cliqThreadId, imageUrl, cliqNotification);
		}
	}

	public static boolean shouldRunAiReviewForEvent(String triggerMode, String triggerLabel, boolean runOnSync, String eventNameRaw, String actionRaw, String prLabelsRaw)
	{
		return shouldRunAiReviewForEvent(triggerMode, triggerLabel, runOnSync, eventNameRaw, actionRaw, prLabelsRaw, "");
	}

	public static boolean shouldRunAiReviewForEvent(String triggerMode, String triggerLabel, boolean runOnSync, String eventNameRaw, String actionRaw, String prLabelsRaw, String fullReviewLabel)
	{
		if(!"pull_request".equals(eventNameRaw) && !"pull_request_target".equals(eventNameRaw))
			return false;

		// The "ready for merge" full-review gate is independent of trigger mode: whenever this
		// exact label is freshly applied to the PR, always run the review (it is an explicit,
		// human-initiated request for a final pre-merge check), regardless of auto/label mode.
		if("labeled".equals(actionRaw) && fullReviewLabel != null && !fullReviewLabel.isBlank() && hasLabel(prLabelsRaw, fullReviewLabel))
			return true;

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

	public static AiReviewDecision evaluateAiReviewDecision(String repository, String prNumber, String pullRequestTitle, String pullRequestBody, String pullRequestUrl, String pullRequestDiffUrl, String pullRequestBaseSha, String pullRequestHeadSha, String incrementalBaseSha, String githubToken)
	{
		String aiToken = (String) System.getenv("AI_REVIEW_TOKEN");
		String modelFromEnv = defaultIfBlank(System.getenv("AI_REVIEW_MODEL"), "");
		String apiUrlFromEnv = defaultIfBlank(System.getenv("AI_REVIEW_API_URL"), "");

		if(aiToken == null || aiToken.isBlank())
			return new AiReviewDecision(false, "AI Review Gate failed", "AI review token is missing.");
		if(githubToken == null || githubToken.isBlank())
			return new AiReviewDecision(false, "AI Review Gate failed", "GITHUB_TOKEN is missing.");

		// When an incremental base is available (synchronize event with a usable previous head),
		// diff previousHead...head so only the newly pushed commits are reviewed, instead of the
		// full base...head diff which would re-flag issues from every prior commit on the PR.
		boolean isIncremental = incrementalBaseSha != null && !incrementalBaseSha.isBlank();
		String diffBaseSha = isIncremental ? incrementalBaseSha : pullRequestBaseSha;

		DiffFetchResult diffResult = fetchPullRequestDiffWithRetries(repository, prNumber, pullRequestDiffUrl, diffBaseSha, pullRequestHeadSha, githubToken, isIncremental);
		if((diffResult.diff == null || diffResult.diff.isBlank()) && isIncremental)
		{
			// The incremental range came back empty (e.g. the "before" SHA is not an ancestor
			// reachable via the compare API, or the previous head was already identical to a
			// point on the new history - can happen after a rebase/force-push edge case that
			// still produced a real, non-zero before SHA). Fall back to the full PR diff rather
			// than silently reporting "no changes" when the PR clearly has content.
			debug("Incremental diff (before=" + trimTo(defaultIfBlank(incrementalBaseSha, "?"), 12) + " head=" + trimTo(defaultIfBlank(pullRequestHeadSha, "?"), 12) + ") was empty. Falling back to full base...head diff.");
			isIncremental = false;
			diffBaseSha = pullRequestBaseSha;
			diffResult = fetchPullRequestDiffWithRetries(repository, prNumber, pullRequestDiffUrl, diffBaseSha, pullRequestHeadSha, githubToken, false);
		}
		if(diffResult.diff == null || diffResult.diff.isBlank())
		{
			if(diffResult.confirmedNoChangedFiles)
				return new AiReviewDecision(true, "success", "AI review skipped: no file changes detected", "GitHub confirmed zero changed files between base " + trimTo(defaultIfBlank(diffBaseSha, "?"), 12) + " and head " + trimTo(defaultIfBlank(pullRequestHeadSha, "?"), 12) + ". Nothing to review.");
			return new AiReviewDecision(false, "failure", "AI Review Gate failed", "Unable to fetch PR diff from GitHub after retries. Failing AI review in strict mode.");
		}
		String diff = diffResult.diff;

		String userPrompt = buildAiPrompt(repository, prNumber, pullRequestTitle, pullRequestBody, pullRequestUrl, diff, isIncremental);
		String provider = detectAiProvider(aiToken, apiUrlFromEnv);
		String model = resolveModelForProvider(provider, modelFromEnv);
		String apiUrl = resolveApiUrlForProvider(provider, apiUrlFromEnv);
		String systemPrompt = "You are a strict PR reviewer. Respond with plain text only using this exact structure: RESULT: PASS or FAIL, SUMMARY: one short line, DETAILS: numbered points (1., 2., 3.). For each detail point include: FILE: <path>, LINE: <line number>, ISSUE: <what is wrong>, FIX: <what to change>. Use file paths and line numbers from the provided diff hunks. If an exact line cannot be determined, use LINE: n/a. "
			+ "DIFF FORMAT RULES (critical, read carefully): The diff uses standard unified diff format. Lines starting with '-' were REMOVED and are NO LONGER PRESENT in the code after this change; lines starting with '+' were ADDED and represent the CURRENT/NEW state of the code; lines starting with a space are unchanged context shown for reference only. You MUST evaluate the code quality, security, and correctness of ONLY the resulting/final state of the code, i.e. the '+' lines and unchanged context lines. NEVER report an issue, vulnerability, or violation whose offending code appears ONLY on a '-' (removed) line \u2014 if a problem such as an XSS sink, hardcoded secret, or bad pattern was deleted (shown as '-') and is not also present as a '+' or unchanged line elsewhere, that means it was FIXED, and you must treat it as resolved, not as an outstanding issue. Before citing any FILE/LINE/ISSUE, double-check that the exact offending code you are citing is present in a '+' or unchanged context line in the CURRENT diff, not merely mentioned in a '-' line. "
			+ "Fail when there are critical or high severity issues related to security, data loss risk, breaking regressions, missing critical validation/error handling, or missing critical tests for changed logic. These validation/error-handling/test requirements apply only to files that contain executable logic (for example JavaScript, TypeScript, Java, Python, form submission handlers, or API calls). Do not require validation, error handling, or test coverage for static content changes such as plain HTML markup, CSS-only changes, Markdown, documentation, or JSON/YAML configuration files that do not introduce new logic; review those only for correctness, broken links or references, and security concerns actually present in the diff. If a file has no applicable issues, do not fabricate a finding for it, and do not penalize the PR for lacking tests or validation that would not make sense for the type of file changed. When every changed file is free of genuine issues, respond with RESULT: PASS and a DETAILS list stating there were no issues found. When the provided diff is explicitly marked as an incremental review (only the most recently pushed commits, not the full pull request), judge ONLY the changes present in that diff: do not fail the review for missing tests, validation, or fixes that would only make sense to evaluate against the full cumulative PR, and do not re-raise issues that are not present in the given incremental diff.";

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

	// Sentinel returned when the PR's /files endpoint responds 200 with a definitively empty
	// file list. That is a real, authoritative answer from GitHub (not a transient failure),
	// so callers must treat it differently from "" (which means "endpoint didn't work / try again").
	public static final String NO_CHANGED_FILES_SENTINEL = "\u0000NO_CHANGED_FILES\u0000";

	public static String fetchPullRequestDiff(String repository, String prNumber, String pullRequestDiffUrl, String pullRequestBaseSha, String pullRequestHeadSha, String githubToken, boolean isIncremental)
	{
		try
		{
			// SHA-pinned compare API is tried FIRST. It is explicitly scoped to the exact
			// base/head commit SHAs captured at event time, so it cannot return a stale,
			// previously-cached diff the way the PR-number-scoped endpoints below can
			// immediately after a fresh "synchronize" push (GitHub lazily recomputes the
			// PR-level diff/mergeability and can briefly serve the previous head's diff).
			if(pullRequestBaseSha != null && !pullRequestBaseSha.isBlank() && pullRequestHeadSha != null && !pullRequestHeadSha.isBlank())
			{
				HashMap<String, String> compareHeaders = new HashMap<String, String>();
				compareHeaders.put("Accept", "application/vnd.github.v3.diff");
				compareHeaders.put("Authorization", "Bearer " + githubToken);
				String compareUrl = "https://api.github.com/repos/" + repository + "/compare/" + pullRequestBaseSha + "..." + pullRequestHeadSha;
				HttpResult compareResponse = sendHttpRequest("GET", compareUrl, null, compareHeaders);
				debug("AI diff fetch via compare API status=" + compareResponse.status + " base=" + pullRequestBaseSha + " head=" + pullRequestHeadSha + " incremental=" + isIncremental);
				if(compareResponse.status >= 200 && compareResponse.status <= 299 && compareResponse.body != null && !compareResponse.body.isBlank())
				{
					// GitHub's compare API is known to occasionally serve a stale/cached diff body
					// for a SHA range requested in quick succession after a fresh push (edge-cache
					// eventual consistency), which is indistinguishable from a genuine 200 response
					// and was silently trusted here before. Cross-check the file paths present in
					// this diff against the PR's live current-head files list (which is recomputed
					// per request and does not suffer the same caching lag). If the compare-API diff
					// references files that are not present in the live head's changed-files set for
					// a non-incremental (full base...head) fetch, treat it as stale and fall through
					// to the PR-scoped endpoints below instead of handing stale content to the AI.
					if(!isIncremental && isCompareDiffStale(repository, prNumber, githubToken, compareResponse.body))
					{
						debug("AI diff fetch: compare API diff for head=" + pullRequestHeadSha + " looks stale relative to the live PR files list. Discarding and falling back to PR-scoped diff endpoints.");
					}
					else
					{
						return compareResponse.body;
					}
				}
				if(compareResponse.status >= 200 && compareResponse.status <= 299 && isEmptyJsonArrayBody(compareResponse.body))
					return NO_CHANGED_FILES_SENTINEL;

				// The .diff media type returns an empty 2xx body BOTH when GitHub hasn't finished
				// indexing the range yet, AND when the range is confirmed to have zero file-level
				// changes (e.g. a merge commit, or an incremental before...head range that nets to
				// no textual diff). Those two cases are indistinguishable from this response alone,
				// which is exactly what caused 10 full retries (~80s) to be burned in incremental
				// mode against a range that would never resolve. Disambiguate immediately using the
				// JSON compare API (same base/head, default Accept), which always reports an
				// authoritative "files" array/count regardless of indexing lag on the .diff endpoint.
				if(compareResponse.status >= 200 && compareResponse.status <= 299 && (compareResponse.body == null || compareResponse.body.isBlank()))
				{
					HashMap<String, String> jsonCompareHeaders = new HashMap<String, String>();
					jsonCompareHeaders.put("Accept", "application/vnd.github+json");
					jsonCompareHeaders.put("Authorization", "Bearer " + githubToken);
					HttpResult jsonCompareResponse = sendHttpRequest("GET", compareUrl, null, jsonCompareHeaders);
					debug("AI diff fetch: empty .diff body, checking JSON compare API status=" + jsonCompareResponse.status);
					if(jsonCompareResponse.status >= 200 && jsonCompareResponse.status <= 299 && jsonCompareResponse.body != null)
					{
						int changedFiles = countFilenameEntries(jsonCompareResponse.body);
						if(changedFiles == 0 && jsonCompareResponse.body.contains("\"status\""))
						{
							// GitHub authoritatively confirms this exact SHA range has no changed
							// files (common for incremental before...head ranges that collapse to
							// nothing, e.g. a merge commit or a no-op push). Stop retrying now
							// instead of exhausting the full retry budget for a result that will
							// never change.
							debug("AI diff fetch: JSON compare API confirms zero changed files for this SHA range. Not retrying further.");
							return NO_CHANGED_FILES_SENTINEL;
						}
					}
				}
			}

			// The files API, pulls/{n} diff-accept endpoint, and diff_url are all scoped to the
			// PR NUMBER, not to a specific SHA range - they always reflect base...currentHead,
			// never previousHead...currentHead. They are correct fallbacks for a full-PR diff,
			// but would silently defeat incremental (delta-only) review by re-introducing the
			// full cumulative diff whenever the SHA-pinned compare API above has a transient
			// hiccup. So skip them entirely in incremental mode and let the retry loop keep
			// retrying the compare API instead of falling back to the wrong range.
			if(isIncremental)
				return "";

			// Files API is also SHA-independent-but-live; it reflects the PR's current head,
			// recomputed per request, and rarely suffers the same diff-cache lag as the
			// pulls/{n} diff-accept endpoint below. It is also the ONLY one of the four
			// strategies that can distinguish "endpoint not ready yet" (non-2xx/empty body)
			// from "GitHub confirms zero changed files" (2xx with an empty [] array) - the
			// other three strategies return an empty diff body in both cases and cannot tell
			// them apart, which is why the zero-files signal is detected here specifically.
			String filesApiDiff = fetchPullRequestDiffFromFilesApi(repository, prNumber, githubToken);
			if(filesApiDiff == NO_CHANGED_FILES_SENTINEL)
				return NO_CHANGED_FILES_SENTINEL;
			if(filesApiDiff != null && !filesApiDiff.isBlank())
				return filesApiDiff;

			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Accept", "application/vnd.github.v3.diff");
			headers.put("Authorization", "Bearer " + githubToken);
			HttpResult response = sendHttpRequest("GET", "https://api.github.com/repos/" + repository + "/pulls/" + prNumber, null, headers);
			debug("AI diff fetch via pulls API (diff accept) status=" + response.status);
			if(response.status >= 200 && response.status <= 299 && response.body != null && !response.body.isBlank())
				return response.body;

			if(pullRequestDiffUrl != null && !pullRequestDiffUrl.isBlank())
			{
				HashMap<String, String> diffHeaders = new HashMap<String, String>();
				diffHeaders.put("Accept", "application/vnd.github.v3.diff");
				diffHeaders.put("Authorization", "Bearer " + githubToken);
				HttpResult diffUrlResponse = sendHttpRequest("GET", pullRequestDiffUrl, null, diffHeaders);
				debug("AI diff fetch via pullRequestDiffUrl status=" + diffUrlResponse.status);
				if(diffUrlResponse.status >= 200 && diffUrlResponse.status <= 299 && diffUrlResponse.body != null && !diffUrlResponse.body.isBlank())
					return diffUrlResponse.body;
			}
		}
		catch(Exception e)
		{
			System.err.println("Unable to fetch PR diff: " + e.getMessage());
		}
		return "";
	}

	public static class DiffFetchResult
	{
		public String diff;
		public boolean confirmedNoChangedFiles;

		public DiffFetchResult(String diff, boolean confirmedNoChangedFiles)
		{
			this.diff = diff == null ? "" : diff;
			this.confirmedNoChangedFiles = confirmedNoChangedFiles;
		}
	}

	public static DiffFetchResult fetchPullRequestDiffWithRetries(String repository, String prNumber, String pullRequestDiffUrl, String pullRequestBaseSha, String pullRequestHeadSha, String githubToken, boolean isIncremental)
	{
		// Pushing a new commit straight to a branch that already has an open PR fires the
		// "synchronize" webhook almost instantly, but GitHub's backend needs a short window
		// to finish indexing the new head commit before ANY diff endpoint (compare API,
		// files API, or the pulls-diff API) will recognize it - during that window all three
		// can return 404/empty even though the push itself succeeded. Verifying the head SHA
		// is resolvable before hammering the diff endpoints, plus a longer/slower backoff,
		// closes that race instead of giving up and failing the whole AI review in strict mode.
		int maxAttempts = 10;
		long baseDelayMs = 3000L;
		long maxDelayMs = 15000L;

		if(pullRequestHeadSha != null && !pullRequestHeadSha.isBlank())
			waitForCommitAvailability(repository, pullRequestHeadSha, githubToken, maxAttempts, baseDelayMs, maxDelayMs);

		for(int attempt = 1; attempt <= maxAttempts; attempt++)
		{
			String diff = fetchPullRequestDiff(repository, prNumber, pullRequestDiffUrl, pullRequestBaseSha, pullRequestHeadSha, githubToken, isIncremental);

			// GitHub gave a definitive, authoritative "zero changed files" answer (HTTP 200,
			// valid empty [] array). This is stable and will not change on retry, unlike a
			// transient 404/empty-body while a commit is still being indexed - so stop
			// immediately instead of burning the remaining attempts and retry delays.
			if(diff == NO_CHANGED_FILES_SENTINEL)
			{
				debug("AI diff fetch attempt " + attempt + "/" + maxAttempts + ": GitHub confirmed zero changed files for base=" + pullRequestBaseSha + " head=" + pullRequestHeadSha + " incremental=" + isIncremental + ". Not retrying further.");
				return new DiffFetchResult("", true);
			}

			if(diff != null && !diff.isBlank())
				return new DiffFetchResult(diff, false);
			if(attempt < maxAttempts)
			{
				long delay = Math.min(baseDelayMs * attempt, maxDelayMs);
				debug("AI diff fetch attempt " + attempt + "/" + maxAttempts + " failed (incremental=" + isIncremental + "). Retrying in " + delay + "ms...");
				try
				{
					Thread.sleep(delay);
				}
				catch(InterruptedException ie)
				{
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
		return new DiffFetchResult("", false);
	}

	// Polls GET /repos/{repo}/commits/{sha} until GitHub's API acknowledges the head commit
	// exists (200), or the same retry budget is exhausted. This avoids wasting the diff-fetch
	// retry loop on repeated 404s while GitHub is still indexing a just-pushed commit.
	public static void waitForCommitAvailability(String repository, String headSha, String githubToken, int maxAttempts, long baseDelayMs, long maxDelayMs)
	{
		try
		{
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Accept", "application/vnd.github+json");
			headers.put("Authorization", "Bearer " + githubToken);
			String endpoint = "https://api.github.com/repos/" + repository + "/commits/" + headSha;

			for(int attempt = 1; attempt <= maxAttempts; attempt++)
			{
				HttpResult response = sendHttpRequest("GET", endpoint, null, headers);
				debug("Commit availability check attempt " + attempt + "/" + maxAttempts + " status=" + response.status + " sha=" + headSha);
				if(response.status >= 200 && response.status <= 299)
					return;
				long delay = Math.min(baseDelayMs * attempt, maxDelayMs);
				try
				{
					Thread.sleep(delay);
				}
				catch(InterruptedException ie)
				{
					Thread.currentThread().interrupt();
					return;
				}
			}
			debug("Commit " + headSha + " was not confirmed available via GitHub API before diff fetch retries began. Proceeding anyway.");
		}
		catch(Exception e)
		{
			debug("Commit availability check failed with exception, proceeding to diff fetch retries anyway: " + e.getMessage());
		}
	}

	// Extracts the set of file paths touched by a unified diff (lines starting with "diff --git
	// a/<path> b/<path>"), used to sanity-check a compare-API diff against the PR's live files list.
	public static java.util.Set<String> extractDiffFilePaths(String diff)
	{
		java.util.Set<String> paths = new java.util.HashSet<String>();
		if(diff == null || diff.isBlank())
			return paths;
		Matcher matcher = Pattern.compile("(?m)^diff --git a/(.+?) b/(.+?)$").matcher(diff);
		while(matcher.find())
			paths.add(matcher.group(2));
		return paths;
	}

	// Returns true when the file paths present in a compare-API diff body do not overlap at all
	// with the PR's live current-head changed-files list, which is the practical signature of a
	// stale/cached compare-API response (GitHub's compare endpoint can briefly serve a diff body
	// computed against a previous head after a rapid successive push). A live files-API call is
	// used as the source of truth since it is recomputed per request rather than cached per SHA
	// range. Fails "open" (returns false / not-stale) on any error, empty diff, or empty live
	// files list, since strict mode should only discard a diff when we have positive evidence it
	// disagrees with the live PR state, never on inconclusive data.
	public static boolean isCompareDiffStale(String repository, String prNumber, String githubToken, String compareDiffBody)
	{
		try
		{
			java.util.Set<String> diffPaths = extractDiffFilePaths(compareDiffBody);
			if(diffPaths.isEmpty())
				return false;

			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Accept", "application/vnd.github+json");
			headers.put("Authorization", "Bearer " + githubToken);
			String endpoint = "https://api.github.com/repos/" + repository + "/pulls/" + prNumber + "/files?per_page=100";
			HttpResult response = sendHttpRequest("GET", endpoint, null, headers);
			if(response.status < 200 || response.status > 299 || response.body == null || response.body.isBlank())
				return false;

			Matcher filenameMatcher = Pattern.compile("\\\"filename\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\\\"])*)\\\"").matcher(response.body);
			java.util.Set<String> liveFilePaths = new java.util.HashSet<String>();
			while(filenameMatcher.find())
				liveFilePaths.add(jsonUnescape(filenameMatcher.group(1)));
			if(liveFilePaths.isEmpty())
				return false;

			for(String path : diffPaths)
			{
				if(liveFilePaths.contains(path))
					return false; // at least one file overlaps - not stale
			}
			// None of the compare-API diff's file paths appear in the live PR files list at all.
			return true;
		}
		catch(Exception e)
		{
			debug("Compare-diff staleness check failed, assuming not stale: " + e.getMessage());
			return false;
		}
	}

	public static String fetchPullRequestDiffFromFilesApi(String repository, String prNumber, String githubToken)
	{
		try
		{
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Accept", "application/vnd.github+json");
			headers.put("Authorization", "Bearer " + githubToken);

			StringBuilder combined = new StringBuilder();
			int totalFiles = 0;
			int page = 1;
			int maxPages = 10; // hard safety cap = 1000 files
			while(page <= maxPages)
			{
				String endpoint = "https://api.github.com/repos/" + repository + "/pulls/" + prNumber + "/files?per_page=100&page=" + page;
				HttpResult response = sendHttpRequest("GET", endpoint, null, headers);
				debug("AI diff fetch via pulls files API page=" + page + " status=" + response.status);
				if(response.status < 200 || response.status > 299 || response.body == null || response.body.isBlank())
					break;

				int countOnPage = countFilenameEntries(response.body);
				if(countOnPage == 0)
				{
					// A 2xx response with a valid, parseable empty array on page 1 is GitHub's
					// authoritative answer, not a sign the endpoint is still warming up. Only
					// trust this signal on page 1: a trailing empty page after earlier pages
					// already returned files just means pagination ended normally.
					if(page == 1 && isEmptyJsonArrayBody(response.body))
						return NO_CHANGED_FILES_SENTINEL;
					break;
				}

				String synthesizedDiff = synthesizeUnifiedDiffFromFilesResponse(response.body);
				if(synthesizedDiff != null && !synthesizedDiff.isBlank())
					combined.append(synthesizedDiff);

				totalFiles += countOnPage;
				if(countOnPage < 100)
					break; // last page reached
				page++;
			}

			if(totalFiles == 0)
				return "";
			debug("AI diff fetch via pulls files API totalFiles=" + totalFiles + " pages=" + page);
			return combined.toString();
		}
		catch(Exception e)
		{
			System.err.println("Unable to fetch PR files for diff synthesis: " + e.getMessage());
		}
		return "";
	}

	// True only for a well-formed, empty JSON array response body (e.g. "[]", possibly with
	// surrounding whitespace). Used to make sure the zero-files fast-path is never taken for a
	// malformed/truncated/non-JSON body that merely happens to contain no "filename" keys.
	public static boolean isEmptyJsonArrayBody(String body)
	{
		if(body == null)
			return false;
		return body.trim().equals("[]");
	}

	public static int countFilenameEntries(String body)
	{
		if(body == null || body.isBlank())
			return 0;
		Matcher matcher = Pattern.compile("\\\"filename\\\"\\s*:\\s*\\\"").matcher(body);
		int count = 0;
		while(matcher.find())
			count++;
		return count;
	}

	public static String synthesizeUnifiedDiffFromFilesResponse(String body)
	{
		if(body == null || body.isBlank())
			return "";
		Matcher entryMatcher = Pattern.compile("\\\"filename\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\\\"])*)\\\".*?\\\"status\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\\\"])*)\\\".*?(?:\\\"patch\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\\\"])*)\\\")?", Pattern.DOTALL).matcher(body);
		StringBuilder sb = new StringBuilder();
		int count = 0;
		while(entryMatcher.find())
		{
			String fileName = jsonUnescape(defaultIfBlank(entryMatcher.group(1), ""));
			String status = jsonUnescape(defaultIfBlank(entryMatcher.group(2), ""));
			String patch = jsonUnescape(defaultIfBlank(entryMatcher.group(3), ""));
			if(fileName.isBlank())
				continue;
			count++;
			sb.append("diff --git a/").append(fileName).append(" b/").append(fileName).append("\n");
			if("added".equalsIgnoreCase(status))
				sb.append("new file mode 100644\n");
			if("removed".equalsIgnoreCase(status))
				sb.append("deleted file mode 100644\n");
			sb.append("--- a/").append(fileName).append("\n");
			sb.append("+++ b/").append(fileName).append("\n");
			if(patch != null && !patch.isBlank())
				sb.append(patch).append("\n");
			else
				sb.append("@@\n").append("[UNREVIEWABLE: No textual patch available from GitHub for this file (binary file or diff too large). Do not report issues for this file; instead list it once under DETAILS as FILE: ").append(fileName).append(", LINE: n/a, ISSUE: file could not be reviewed (no patch content available), FIX: review manually.]\n");
		}
		if(count == 0)
			return "";
		return sb.toString();
	}

	public static String buildAiPrompt(String repository, String prNumber, String pullRequestTitle, String pullRequestBody, String pullRequestUrl, String diff, boolean isIncremental)
	{
		StringBuilder prompt = new StringBuilder();
		prompt.append("Repository: ").append(defaultIfBlank(repository, "")).append("\\n");
		prompt.append("PR Number: ").append(defaultIfBlank(prNumber, "")).append("\\n");
		prompt.append("PR Title: ").append(defaultIfBlank(pullRequestTitle, "")).append("\\n");
		prompt.append("PR URL: ").append(defaultIfBlank(pullRequestUrl, "")).append("\\n\\n");
		prompt.append("PR Description:\\n").append(defaultIfBlank(pullRequestBody, "")).append("\\n\\n");
		if(isIncremental)
			prompt.append("Review Scope: INCREMENTAL. The diff below contains ONLY the commits pushed in the most recent update to this pull request, not the full cumulative pull request diff. Judge only these changes.\\n\\n");

		int maxDiffChars = 120000;
		DiffTruncationResult truncationResult = truncateDiffByFileBoundary(defaultIfBlank(diff, ""), maxDiffChars);
		prompt.append("Diff:\\n").append(truncationResult.diffText);
		if(!truncationResult.omittedFiles.isEmpty())
		{
			prompt.append("\\n\\n[NOTE: The following ").append(truncationResult.omittedFiles.size())
				.append(" file(s) could not be included in this review due to overall diff size limits and were NOT reviewed. Do not report issues for them, do not assume they are correct, and list each once under DETAILS as FILE: <name>, LINE: n/a, ISSUE: file omitted from AI review due to PR size limits, FIX: review manually. Omitted files: ")
				.append(String.join(", ", truncationResult.omittedFiles)).append("]");
		}
		return prompt.toString();
	}

	public static class DiffTruncationResult
	{
		public String diffText;
		public ArrayList<String> omittedFiles;
		public DiffTruncationResult(String diffText, ArrayList<String> omittedFiles)
		{
			this.diffText = diffText;
			this.omittedFiles = omittedFiles;
		}
	}

	public static DiffTruncationResult truncateDiffByFileBoundary(String diff, int maxChars)
	{
		ArrayList<String> omitted = new ArrayList<String>();
		if(diff == null || diff.isBlank())
			return new DiffTruncationResult("", omitted);
		if(diff.length() <= maxChars)
			return new DiffTruncationResult(diff, omitted);

		String[] fileBlocks = diff.split("(?=^diff --git )", 0);
		StringBuilder kept = new StringBuilder();
		int runningLength = 0;
		boolean limitReached = false;
		Pattern filenamePattern = Pattern.compile("^diff --git a/(.*?) b/");
		for(String block : fileBlocks)
		{
			if(block == null || block.isBlank())
				continue;
			if(!limitReached && runningLength + block.length() <= maxChars)
			{
				kept.append(block);
				runningLength += block.length();
			}
			else
			{
				limitReached = true;
				Matcher m = filenamePattern.matcher(block.trim());
				if(m.find())
					omitted.add(m.group(1));
				else
					omitted.add("(unnamed file)");
			}
		}
		if(kept.length() == 0)
			kept.append(trimTo(diff, maxChars));
		return new DiffTruncationResult(kept.toString(), omitted);
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
		String value = extractJsonStringField(body, "content");
		if(value != null)
			return value;
		return "";
	}

	public static String extractClaudeContent(String body)
	{
		if(body == null || body.isBlank())
			return "";
		String value = extractJsonStringField(body, "text");
		if(value != null)
			return value;
		return "";
	}

	public static String extractGeminiContent(String body)
	{
		if(body == null || body.isBlank())
			return "";
		String value = extractJsonStringField(body, "text");
		if(value != null)
			return value;
		return "";
	}

	public static String extractJsonStringField(String json, String fieldName)
	{
		if(json == null || json.isBlank() || fieldName == null || fieldName.isBlank())
			return null;
		String quotedField = "\"" + fieldName + "\"";
		int searchFrom = 0;
		while(true)
		{
			int keyIndex = json.indexOf(quotedField, searchFrom);
			if(keyIndex < 0)
				return null;
			int colonIndex = json.indexOf(':', keyIndex + quotedField.length());
			if(colonIndex < 0)
				return null;

			int valueStart = colonIndex + 1;
			while(valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart)))
				valueStart++;

			if(valueStart >= json.length())
				return null;
			if(json.charAt(valueStart) != '"')
			{
				searchFrom = keyIndex + quotedField.length();
				continue;
			}

			StringBuilder escapedValue = new StringBuilder();
			boolean escaping = false;
			for(int i = valueStart + 1; i < json.length(); i++)
			{
				char c = json.charAt(i);
				if(escaping)
				{
					escapedValue.append('\\').append(c);
					escaping = false;
					continue;
				}
				if(c == '\\')
				{
					escaping = true;
					continue;
				}
				if(c == '"')
					return jsonUnescape(escapedValue.toString());
				escapedValue.append(c);
			}

			return null;
		}
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
		source = trimTo(source, 12000);

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
		source = trimTo(source, 12000);
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

	// Cliq's message card supports a limited number of characters, and large PRs (multiple files/hunks)
	// can produce AI review details that are too long or unreadable as a single chat card.
	// So Cliq only gets a short redirect message; the full details are always posted in full on the
	// GitHub PR comment. Mirrors the compact GitLab Informer Cliq card format (no inline detail dump).
	public static String buildAiFailureCliqNotification(String prNumber, String pullRequestUrl, String summary, boolean detailsPostedAsPrComment, int summaryMaxLength, String actionsRunUrl)
	{
		StringBuilder msg = new StringBuilder();
		msg.append(":x: **AI Review Failed**\n");
		if(pullRequestUrl != null && !pullRequestUrl.isBlank())
			msg.append("[PR #").append(defaultIfBlank(prNumber, "")).append("](").append(pullRequestUrl).append(")");
		else
			msg.append("PR #").append(defaultIfBlank(prNumber, ""));
		msg.append("\n\n");
		if(detailsPostedAsPrComment && pullRequestUrl != null && !pullRequestUrl.isBlank())
			msg.append("See full details in the [PR comment](").append(pullRequestUrl).append(").");
		else if(pullRequestUrl != null && !pullRequestUrl.isBlank())
			msg.append("Open the [pull request](").append(pullRequestUrl).append(") on GitHub to view details.");
		else
			msg.append("Open the pull request on GitHub to view details.");
		return msg.toString();
	}

	public static int parseIntOrDefault(String value, int fallback)
	{
		try
		{
			if(value == null || value.isBlank())
				return fallback;
			return Integer.parseInt(value.trim());
		}
		catch(Exception e)
		{
			return fallback;
		}
	}

	public static String buildActionsRunUrl(String repository)
	{
		String serverUrl = defaultIfBlank(System.getenv("GITHUB_SERVER_URL"), "https://github.com");
		String runId = defaultIfBlank(System.getenv("GITHUB_RUN_ID"), "");
		if(repository == null || repository.isBlank() || runId.isBlank())
			return "";
		return serverUrl + "/" + repository + "/actions/runs/" + runId;
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
			String message = failureMessage;
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

	public static void setAiReviewCheckRun(String repository, String headSha, String githubToken, String checkName, String conclusionRaw, String summary, String details)
	{
		try
		{
			String conclusion = defaultIfBlank(conclusionRaw, "success").trim().toLowerCase();
			if(!("success".equals(conclusion) || "failure".equals(conclusion) || "neutral".equals(conclusion) || "cancelled".equals(conclusion) || "timed_out".equals(conclusion) || "skipped".equals(conclusion) || "action_required".equals(conclusion) || "stale".equals(conclusion)))
				conclusion = "success";
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
