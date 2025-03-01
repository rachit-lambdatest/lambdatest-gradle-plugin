package io.github.lambdatest.gradle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;

/**
 * Uploader task class for the LambdaTest Gradle plugin that handles uploading of APK . This task
 * manages the upload of applications and test suites.
 *
 * <p>This task coordinates between {@link AppUploader} and {@link TestSuiteUploader}, to upload the
 * apps to Lmabdatest.
 */
public class LambdaUploaderTask extends DefaultTask {

    private static final Logger logger = Logging.getLogger(LambdaUploaderTask.class);
    private String username;
    private String accessKey;
    private String appFilePath;
    private String testSuiteFilePath;
    private Boolean isVirtualDevice = false;

    @TaskAction
    public void uploadApkToLambdaTest() {
        // Generated after upload of app and test suite
        String appId;
        String testSuiteId;
        CompletableFuture<String> appIdFuture = null;
        CompletableFuture<String> testSuiteIdFuture = null;
        logger.lifecycle("Starting LambdaTest APK Uploader task...");

        if (appFilePath != null) {
            logger.lifecycle("Uploading app ...");
            AppUploader appUploader =
                    new AppUploader(username, accessKey, appFilePath, isVirtualDevice);
            appIdFuture = appUploader.uploadAppAsync();
        }

        if (testSuiteFilePath != null) {
            logger.lifecycle("Uploading test suite ...");
            TestSuiteUploader testSuiteUploader =
                    new TestSuiteUploader(username, accessKey, testSuiteFilePath, isVirtualDevice);
            testSuiteIdFuture = testSuiteUploader.uploadTestSuiteAsync();
        }

        try {
            if (appIdFuture != null) {
                appId = appIdFuture.join();
                logger.lifecycle("\u001B[32mApp uploaded successfully with ID: {}\u001B[0m", appId);
            }

            if (testSuiteIdFuture != null) {
                testSuiteId = testSuiteIdFuture.join();
                logger.lifecycle(
                        "\u001B[32mTest suite uploaded successfully with ID: {}\u001B[0m",
                        testSuiteId);
            }
        } catch (CompletionException e) {
            logger.error("Failed to execute LambdaTest APK Uploader task : {}", e);
            throw new RuntimeException(e);
        }

        logger.lifecycle("Completed LambdaTest APK Uploader task ...");
    }

    // Setter functions for the task
    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setAppFilePath(String appFilePath) {
        this.appFilePath = appFilePath;
    }

    public void setTestSuiteFilePath(String testSuiteFilePath) {
        this.testSuiteFilePath = testSuiteFilePath;
    }

    public void setIsVirtualDevice(Boolean isVirtualDevice) {
        this.isVirtualDevice = (isVirtualDevice != null && isVirtualDevice);
    }
}
