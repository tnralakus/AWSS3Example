package com.example.aws;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * Hello world!
 *
 */
public class App {

	private static final String SUFFIX = "/";
	private static final String BUCKET_NAME = "testbuckettaner2";
	private static final String FOLDER_NAME = "testfoldertaner2";
	private static final String FILE = "test.txt";
	private static final String FILE_PATH = "C:\\dev\\workspaces\\AWSExample\\src\\main\\resources";
	private static final String PROFILE = "alakus-gmail";

    public static void main(String[] args) {
        AWSCredentials credentials = new ProfileCredentialsProvider(PROFILE).getCredentials();

        AmazonS3 s3client = new AmazonS3Client(credentials);

        s3client.createBucket(BUCKET_NAME);

		createFolder(s3client, BUCKET_NAME, FOLDER_NAME, SUFFIX);

		uploadFile(s3client, FILE, FILE_PATH, FOLDER_NAME, BUCKET_NAME, SUFFIX);

		showMe(s3client);

		//s3client.deleteObject(BUCKET_NAME, FILE);

		//deleteFolder(BUCKET_NAME, FOLDER_NAME, s3client);
    }

	/**
	 * This method first deletes all the files in given folder and than the
	 * folder itself
	 */
	public static void deleteFolder(String bucketName, String folderName, AmazonS3 client) {
		List<S3ObjectSummary> fileList = client.listObjects(bucketName, folderName).getObjectSummaries();
		for (S3ObjectSummary file : fileList) {
			client.deleteObject(bucketName, file.getKey());
		}
		client.deleteObject(bucketName, folderName);
	}

	private static void showMe(AmazonS3 s3client) {
		for (Bucket bucket : s3client.listBuckets()) {
			System.out.println("BUCKET - " + bucket.getName());
		}
	}

	private static void createFolder(AmazonS3 client, String bucketName, String folderName, String suffix) {
        // create meta-data for your folder and set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        // create empty content
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        // create a PutObjectRequest passing the folder name suffixed by /
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + suffix, emptyContent, metadata);
        // send request to S3 to create folder
        client.putObject(putObjectRequest);
    }

	private static void uploadFile(AmazonS3 client, String file, String path, String folderName, String bucketName, String suffix) {
		String fileName = folderName + SUFFIX + file;
		client.putObject(new PutObjectRequest(bucketName, fileName, new File(path + "\\" + file)).withCannedAcl(CannedAccessControlList.PublicRead));

		client.putObject(new PutObjectRequest(bucketName, file, new File(path + "\\" + file)));
    }
}
