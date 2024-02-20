package com.netbanking.servlet;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

@WebServlet(urlPatterns = { "/requestStatement" })
public class RequestStatementServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String accountNo = null;
		String mobileNo = null;
		String emailAddress = null;
		int month;
		int year;
		String requestStatementQueueUrl = null;
		String requestStatementMessage = null;
		Properties props = null;

		props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("env.properties"));
		requestStatementQueueUrl = props.getProperty("queueURL");

		accountNo = req.getParameter("accountNo");
		mobileNo = req.getParameter("mobileNo");
		emailAddress = req.getParameter("emailAddress");
		month = Integer.parseInt(req.getParameter("month"));
		year = Integer.parseInt(req.getParameter("year"));

		requestStatementMessage = "{\"accountNo \":\"" + accountNo + "\", \"mobileNo\" :\"" + mobileNo
				+ "\",\"month\":\"" + month + "\",\"year\":\"" + year + "\"}";

		// ProfileCredentialsProvider credentialsProvider = new
		// ProfileCredentialsProvider();
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(props.getProperty("aws_access_key_id"),
				props.getProperty("aws_secret_access_key"));
		AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (~/.aws/credentials), and is in valid format.", e);
		}
		AmazonSQS sqs = AmazonSQSClientBuilder.standard().withCredentials(credentialsProvider)
				.withRegion(Regions.AP_SOUTH_1).build();

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon SQS");
		System.out.println("===========================================\n");

		System.out.println("Listing all queues in your account.\n");
		for (String queueUrl : sqs.listQueues().getQueueUrls()) {
			System.out.println("  QueueUrl: " + queueUrl);
		}
		System.out.println();

		System.out.println("Sending a message to MyQueue.\n");

		sqs.sendMessage(new SendMessageRequest(requestStatementQueueUrl, requestStatementMessage));
		resp.getWriter().print(requestStatementMessage);
	}

}
