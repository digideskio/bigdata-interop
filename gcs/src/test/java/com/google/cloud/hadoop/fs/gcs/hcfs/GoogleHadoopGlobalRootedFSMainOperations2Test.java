/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.hadoop.fs.gcs.hcfs;

import static org.apache.hadoop.fs.FileSystemTestHelper.exists;

import com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystemTestHelper;

import org.apache.hadoop.fs.FSMainOperationsBaseTest;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

/**
 * Runs the Hadoop tests in FSMainOperationsBaseTest over the GoogleHadoopFileSystem.
 * Tests that the GoogleHadoopFileSystem obeys the file system contract specified for
 * Hadoop.
 * This class is used to test Hadoop v2 functionality.
 */
@RunWith(JUnit4.class)
public class GoogleHadoopGlobalRootedFSMainOperations2Test
    extends FSMainOperationsBaseTest {

  FileSystemTestHelper helper = new FileSystemTestHelper();

  /**
   * In Hadoop2 this method overrides the abstract method of the same name in
   * FSMainOperationsBaseTest
   * @return
   */
  public FileSystem createFileSystem() throws Exception {
    return GoogleHadoopFileSystemTestHelper.createInMemoryGoogleHadoopGlobalRootedFileSystem();
  }

  /**
   * Copied from FSMainOperationsBaseTest.java with the only changes being throwing an IOException
   * instead of returning false when trying to create directories on top of existing files. This
   * behavior is in-line with HDFS, but differs from LocalFileSystem.
   */
  @Test @Override
  public void testMkdirsFailsForSubdirectoryOfExistingFile() throws Exception {
    Path testDir = helper.getTestRootPath(fSys, "test/hadoop");
    Assert.assertFalse(exists(fSys, testDir));
    fSys.mkdirs(testDir);
    Assert.assertTrue(exists(fSys, testDir));
    
    createFile(helper.getTestRootPath(fSys, "test/hadoop/file"));
    
    Path testSubDir = helper.getTestRootPath(fSys, "test/hadoop/file/subdir");
    try {
      fSys.mkdirs(testSubDir);
      Assert.fail("Should throw IOException.");
    } catch (IOException e) {
      // expected
    }
    Assert.assertFalse(exists(fSys, testSubDir));
    
    Path testDeepSubDir = helper.getTestRootPath(fSys, "test/hadoop/file/deep/sub/dir");
    Assert.assertFalse(exists(fSys, testSubDir));
    try {
      fSys.mkdirs(testDeepSubDir);
      Assert.fail("Should throw IOException.");
    } catch (IOException e) {
      // expected
    }
    Assert.assertFalse(exists(fSys, testDeepSubDir));
  }

  @Test @Override
  public void testWorkingDirectory() throws Exception {
    try {
      super.testWorkingDirectory();
    } catch (AssertionError ae) {
      // Only the last line of the superclass's test should fail; it fails simply because the
      // global-rooted case prefers no authority component.
      Assert.assertEquals(
          "expected:<gsg://test/existingDir> but was:<gsg:/test/existingDir>", ae.getMessage());
    }
  }

  @Test @Override
  public void testListStatusThrowsExceptionForNonExistentFile() throws Exception {
  }

  @Test @Override
  public void testListStatusThrowsExceptionForUnreadableDir() throws Exception {
  }

  @Test @Override
  public void testCopyToLocalWithUseRawLocalFileSystemOption() throws Exception {
  }

  @Test @Override
  public void testWDAbsolute() throws IOException {
  }
}
