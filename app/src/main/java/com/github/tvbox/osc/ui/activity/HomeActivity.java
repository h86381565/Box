Run chmod +x gradlew
Downloading https://mirrors.cloud.tencent.com/gradle/gradle-7.5-bin.zip
...................................................................................................................
Unzipping /home/runner/.gradle/wrapper/dists/gradle-7.5-bin/1bmvna2xxjhfv7365tgdvoqbr/gradle-7.5-bin.zip to /home/runner/.gradle/wrapper/dists/gradle-7.5-bin/1bmvna2xxjhfv7365tgdvoqbr
Set executable permissions for: /home/runner/.gradle/wrapper/dists/gradle-7.5-bin/1bmvna2xxjhfv7365tgdvoqbr/gradle-7.5/bin/gradle

Welcome to Gradle 7.5!

Here are the highlights of this release:
 - Support for Java 18
 - Support for building with Groovy 4
 - Much more responsive continuous builds
 - Improved diagnostics for dependency resolution

For more details see https://docs.gradle.org/7.5/release-notes.html

Starting a Gradle Daemon (subsequent builds will be faster)
WARNING:We recommend using a newer Android Gradle plugin to use compileSdk = 34

This Android Gradle plugin (7.4.2) was tested up to compileSdk = 33

This warning can be suppressed by adding
    android.suppressUnsupportedCompileSdk=34
to this project's gradle.properties

The build will continue, but you are strongly encouraged to update your project to
use a newer Android Gradle Plugin that has been tested with compileSdk = 34
This version only understands SDK XML versions up to 3 but an SDK XML file of version 4 was encountered. This can happen if you use versions of Android Studio and the command-line tools that were released at different times.
Checking the license for package Android SDK Build-Tools 30.0.3 in /usr/local/lib/android/sdk/licenses
License for package Android SDK Build-Tools 30.0.3 accepted.
Preparing "Install Android SDK Build-Tools 30.0.3 (revision: 30.0.3)".
"Install Android SDK Build-Tools 30.0.3 (revision: 30.0.3)" ready.
Installing Android SDK Build-Tools 30.0.3 in /usr/local/lib/android/sdk/build-tools/30.0.3
"Install Android SDK Build-Tools 30.0.3 (revision: 30.0.3)" complete.
"Install Android SDK Build-Tools 30.0.3 (revision: 30.0.3)" finished.
> Task :app:preBuild UP-TO-DATE
> Task :app:preArm64GenericNormalDebugBuild UP-TO-DATE
> Task :app:mergeArm64GenericNormalDebugNativeDebugMetadata NO-SOURCE
> Task :quickjs:preBuild UP-TO-DATE
> Task :quickjs:preDebugBuild UP-TO-DATE
> Task :quickjs:compileDebugAidl NO-SOURCE
> Task :app:compileArm64GenericNormalDebugAidl NO-SOURCE
> Task :app:checkKotlinGradlePluginConfigurationErrors
> Task :quickjs:packageDebugRenderscript NO-SOURCE
> Task :app:compileArm64GenericNormalDebugRenderscript NO-SOURCE

> Task :app:dataBindingMergeDependencyArtifactsArm64GenericNormalDebug
WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/me.jessyan/autosize/1.2.1/a44df9822e0cb91242358f070ef813714fd81c05/autosize-1.2.1.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
 Example of androidX reference: 'androidx/fragment/app/FragmentManager$FragmentLifecycleCallbacks'
 Example of support library reference: 'android/support/v4/app/FragmentManager$FragmentLifecycleCallbacks'
WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-ui/1.3.1/97d6136ea0c4942f67fccdfb77c5099fec02c4c0/media3-ui-1.3.1.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
 Example of androidX reference: 'androidx/media3/ui/PlayerNotificationManager'
 Example of support library reference: 'android/support/v4/media/session/MediaSessionCompat$Token'

> Task :app:generateArm64GenericNormalDebugResValues
> Task :app:generateArm64GenericNormalDebugResources
> Task :quickjs:compileDebugRenderscript NO-SOURCE
> Task :quickjs:generateDebugResValues
> Task :quickjs:generateDebugResources
> Task :quickjs:packageDebugResources
> Task :app:dataBindingTriggerArm64GenericNormalDebug
> Task :app:generateArm64GenericNormalDebugAppInfo
> Task :app:generateArm64GenericNormalDebugBuildConfig
> Task :quickjs:writeDebugAarMetadata
> Task :app:mapArm64GenericNormalDebugSourceSetPaths
> Task :app:createArm64GenericNormalDebugCompatibleScreenManifests
> Task :app:extractDeepLinksArm64GenericNormalDebug
> Task :app:checkArm64GenericNormalDebugAarMetadata
> Task :quickjs:extractDeepLinksDebug

> Task :quickjs:processDebugManifest
package="com.whl.quickjs.android" found in source AndroidManifest.xml: /home/runner/work/Box/Box/quickjs/src/main/AndroidManifest.xml.
Setting the namespace via a source AndroidManifest.xml's package attribute is deprecated.
Please instead set the namespace (or testNamespace) in the module's build.gradle file, as described here: https://developer.android.com/studio/build/configure-app-module#set-namespace
This migration can be done automatically using the AGP Upgrade Assistant, please refer to https://developer.android.com/studio/build/agp-upgrade-assistant for more information.

> Task :app:mergeArm64GenericNormalDebugResources
> Task :quickjs:compileDebugLibraryResources
> Task :app:dataBindingGenBaseClassesArm64GenericNormalDebug

> Task :app:processArm64GenericNormalDebugMainManifest
package="com.github.tvbox.osc" found in source AndroidManifest.xml: /home/runner/work/Box/Box/app/src/main/AndroidManifest.xml.
Setting the namespace via a source AndroidManifest.xml's package attribute is deprecated.
Please instead set the namespace (or testNamespace) in the module's build.gradle file, as described here: https://developer.android.com/studio/build/configure-app-module#set-namespace
This migration can be done automatically using the AGP Upgrade Assistant, please refer to https://developer.android.com/studio/build/agp-upgrade-assistant for more information.

> Task :app:processArm64GenericNormalDebugManifest
> Task :quickjs:generateDebugBuildConfig
> Task :app:processArm64GenericNormalDebugManifestForPackage
> Task :quickjs:javaPreCompileDebug
> Task :quickjs:parseDebugLocalResources
> Task :app:javaPreCompileArm64GenericNormalDebug
> Task :app:mergeArm64GenericNormalDebugShaders
> Task :app:compileArm64GenericNormalDebugShaders NO-SOURCE
> Task :app:generateArm64GenericNormalDebugAssets UP-TO-DATE
> Task :quickjs:mergeDebugShaders
> Task :quickjs:compileDebugShaders NO-SOURCE
> Task :quickjs:generateDebugAssets UP-TO-DATE
> Task :quickjs:packageDebugAssets
> Task :quickjs:generateDebugRFile

> Task :app:mergeArm64GenericNormalDebugAssets
Execution optimizations have been disabled for task ':app:mergeArm64GenericNormalDebugAssets' to ensure correctness due to the following reasons:
  - Additional action of task ':app:mergeArm64GenericNormalDebugAssets' was implemented by the Java lambda 'com.yanzhenjie.andserver.plugin.AndServerPlugin$$Lambda$939/0x00007fa36cb80820'. Reason: Using Java lambdas is not supported as task inputs. Please refer to https://docs.gradle.org/7.5/userguide/validation_problems.html#implementation_unknown for more details about this problem.

> Task :app:processArm64GenericNormalDebugResources
> Task :quickjs:compileDebugJavaWithJavac
> Task :app:processArm64GenericNormalDebugJavaRes NO-SOURCE
> Task :quickjs:processDebugJavaRes NO-SOURCE
> Task :quickjs:bundleLibResDebug NO-SOURCE
> Task :quickjs:bundleLibCompileToJarDebug
> Task :app:compressArm64GenericNormalDebugAssets
> Task :app:checkArm64GenericNormalDebugDuplicateClasses
> Task :quickjs:bundleLibRuntimeToJarDebug
> Task :app:mergeArm64GenericNormalDebugJniLibFolders
> Task :quickjs:mergeDebugJniLibFolders
> Task :quickjs:mergeDebugNativeLibs
> Task :quickjs:copyDebugJniLibsProjectOnly
> Task :app:mergeLibDexArm64GenericNormalDebug
> Task :app:mergeArm64GenericNormalDebugNativeLibs
> Task :app:validateSigningArm64GenericNormalDebug
> Task :app:writeArm64GenericNormalDebugAppMetadata
> Task :app:writeArm64GenericNormalDebugSigningConfigVersions

> Task :app:stripArm64GenericNormalDebugDebugSymbols
Unable to strip the following libraries, packaging them as they are: libalivc_conan.so, libalivcffmpeg.so, libavcodec.so, libavutil.so, libconceal.so, libconscrypt_jni.so, libffmpegJNI.so, libijkffmpeg.so, libijkplayer.so, libijksdl.so, libmedia3ext.so, libp2p.so, libquickjs-android-wrapper.so, librtmp-jni.so, libsaasCorePlayer.so, libsaasDownloader.so, libswresample.so, libswscale.so, libxl_stat.so, libxl_thunder_sdk.so.

> Task :app:kaptGenerateStubsArm64GenericNormalDebugKotlin

Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:2: error: class, interface, or enum expected
> Task :app:kaptArm64GenericNormalDebugKotlin
protected void init() {
          ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:6: error: class, interface, or enum expected
    } catch (Exception ignore) {}
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:9: error: class, interface, or enum expected
    } catch (Exception e) {
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:11: error: class, interface, or enum expected
    }
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:14: error: class, interface, or enum expected
    } catch (Exception e) {
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:15: error: class, interface, or enum expected
        try { AuthUtil.saveActivated(); } catch (Exception ignore) {}
                                        ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:18: error: class, interface, or enum expected
    EventBus.getDefault().register(this);
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:19: error: class, interface, or enum expected
    ControlManager.get().startServer();
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:20: error: class, interface, or enum expected
    App.startWebserver();
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:21: error: class, interface, or enum expected
    initView();
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:22: error: class, interface, or enum expected
    initViewModel();
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:23: error: class, interface, or enum expected
    useCacheConfig = false;
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:24: error: class, interface, or enum expected
    Intent intent = getIntent();
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:25: error: class, interface, or enum expected
    if (intent != null && intent.getExtras() != null) {
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:27: error: class, interface, or enum expected
        useCacheConfig = bundle.getBoolean("useCache", false);
        ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:28: error: class, interface, or enum expected
    }
    ^
Error: /home/runner/work/Box/Box/app/src/main/java/com/github/tvbox/osc/ui/activity/HomeActivity.java:30: error: class, interface, or enum expected
}
^

> Task :app:kaptArm64GenericNormalDebugKotlin FAILED
> Task :app:desugarArm64GenericNormalDebugFileDependencies

FAILURE: Build failed with an exception.

* What went wrong:

Execution failed for task ':app:kaptArm64GenericNormalDebugKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask$KaptExecutionWorkAction
Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0.
   > java.lang.reflect.InvocationTargetException (no error message)


You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.
* Try:

> Run with --info or --debug option to get more log output.
See https://docs.gradle.org/7.5/userguide/command_line_interface.html#sec:command_line_warnings
> Run with --scan to get full insights.


* Exception is:
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:kaptArm64GenericNormalDebugKotlin'.
	at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.lambda$executeIfValid$1(ExecuteActionsTaskExecuter.java:142)
	at org.gradle.internal.Try$Failure.ifSuccessfulOrElse(Try.java:282)
	at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeIfValid(ExecuteActionsTaskExecuter.java:140)
	at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.execute(ExecuteActionsTaskExecuter.java:128)
	at org.gradle.api.internal.tasks.execution.CleanupStaleOutputsExecuter.execute(CleanupStaleOutputsExecuter.java:77)
	at org.gradle.api.internal.tasks.execution.FinalizePropertiesTaskExecuter.execute(FinalizePropertiesTaskExecuter.java:46)
	at org.gradle.api.internal.tasks.execution.ResolveTaskExecutionModeExecuter.execute(ResolveTaskExecutionModeExecuter.java:51)
	at org.gradle.api.internal.tasks.execution.SkipTaskWithNoActionsExecuter.execute(SkipTaskWithNoActionsExecuter.java:57)
	at org.gradle.api.internal.tasks.execution.SkipOnlyIfTaskExecuter.execute(SkipOnlyIfTaskExecuter.java:56)
	at org.gradle.api.internal.tasks.execution.CatchExceptionTaskExecuter.execute(CatchExceptionTaskExecuter.java:36)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.executeTask(EventFiringTaskExecuter.java:77)
Execution optimizations have been disabled for 1 invalid unit(s) of work during this build to ensure correctness.
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:55)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:52)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:204)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:199)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:53)
	at org.gradle.internal.operations.DefaultBuildOperationExecutor.call(DefaultBuildOperationExecutor.java:73)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter.execute(EventFiringTaskExecuter.java:52)
	at org.gradle.execution.plan.LocalTaskNodeExecutor.execute(LocalTaskNodeExecutor.java:69)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:327)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:314)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:307)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:293)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.execute(DefaultPlanExecutor.java:417)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.run(DefaultPlanExecutor.java:339)
	at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
	at org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:48)
Caused by: org.gradle.workers.internal.DefaultWorkerExecutor$WorkExecutionException: A failure occurred while executing org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask$KaptExecutionWorkAction
	at org.gradle.workers.internal.DefaultWorkerExecutor$WorkItemExecution.waitForCompletion(DefaultWorkerExecutor.java:339)
	at org.gradle.internal.work.DefaultAsyncWorkTracker.lambda$waitForItemsAndGatherFailures$2(DefaultAsyncWorkTracker.java:130)
	at org.gradle.internal.Factories$1.create(Factories.java:31)
	at org.gradle.internal.work.DefaultWorkerLeaseService.withoutLocks(DefaultWorkerLeaseService.java:321)
	at org.gradle.internal.work.DefaultWorkerLeaseService.withoutLocks(DefaultWorkerLeaseService.java:304)
	at org.gradle.internal.work.DefaultWorkerLeaseService.withoutLock(DefaultWorkerLeaseService.java:309)
	at org.gradle.internal.work.DefaultAsyncWorkTracker.waitForItemsAndGatherFailures(DefaultAsyncWorkTracker.java:126)
	at org.gradle.internal.work.DefaultAsyncWorkTracker.waitForItemsAndGatherFailures(DefaultAsyncWorkTracker.java:92)
	at org.gradle.internal.work.DefaultAsyncWorkTracker.waitForAll(DefaultAsyncWorkTracker.java:78)
	at org.gradle.internal.work.DefaultAsyncWorkTracker.waitForCompletion(DefaultAsyncWorkTracker.java:66)
	at org.gradle.api.internal.tasks.execution.TaskExecution$3.run(TaskExecution.java:244)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:29)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:26)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.run(DefaultBuildOperationRunner.java:47)
	at org.gradle.internal.operations.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java:68)
	at org.gradle.api.internal.tasks.execution.TaskExecution.executeAction(TaskExecution.java:221)
	at org.gradle.api.internal.tasks.execution.TaskExecution.executeActions(TaskExecution.java:204)
	at org.gradle.api.internal.tasks.execution.TaskExecution.executeWithPreviousOutputFiles(TaskExecution.java:187)
	at org.gradle.api.internal.tasks.execution.TaskExecution.execute(TaskExecution.java:165)
	at org.gradle.internal.execution.steps.ExecuteStep.executeInternal(ExecuteStep.java:89)
	at org.gradle.internal.execution.steps.ExecuteStep.access$000(ExecuteStep.java:40)
	at org.gradle.internal.execution.steps.ExecuteStep$1.call(ExecuteStep.java:53)
	at org.gradle.internal.execution.steps.ExecuteStep$1.call(ExecuteStep.java:50)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:204)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:199)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:53)
	at org.gradle.internal.operations.DefaultBuildOperationExecutor.call(DefaultBuildOperationExecutor.java:73)
	at org.gradle.internal.execution.steps.ExecuteStep.execute(ExecuteStep.java:50)
	at org.gradle.internal.execution.steps.ExecuteStep.execute(ExecuteStep.java:40)
	at org.gradle.internal.execution.steps.RemovePreviousOutputsStep.execute(RemovePreviousOutputsStep.java:68)
	at org.gradle.internal.execution.steps.RemovePreviousOutputsStep.execute(RemovePreviousOutputsStep.java:38)
	at org.gradle.internal.execution.steps.CancelExecutionStep.execute(CancelExecutionStep.java:41)
	at org.gradle.internal.execution.steps.TimeoutStep.executeWithoutTimeout(TimeoutStep.java:74)
	at org.gradle.internal.execution.steps.TimeoutStep.execute(TimeoutStep.java:55)
	at org.gradle.internal.execution.steps.CreateOutputsStep.execute(CreateOutputsStep.java:51)
	at org.gradle.internal.execution.steps.CreateOutputsStep.execute(CreateOutputsStep.java:29)
	at org.gradle.internal.execution.steps.CaptureStateAfterExecutionStep.executeDelegateBroadcastingChanges(CaptureStateAfterExecutionStep.java:124)
	at org.gradle.internal.execution.steps.CaptureStateAfterExecutionStep.execute(CaptureStateAfterExecutionStep.java:80)
	at org.gradle.internal.execution.steps.CaptureStateAfterExecutionStep.execute(CaptureStateAfterExecutionStep.java:58)
	at org.gradle.internal.execution.steps.ResolveInputChangesStep.execute(ResolveInputChangesStep.java:48)
	at org.gradle.internal.execution.steps.ResolveInputChangesStep.execute(ResolveInputChangesStep.java:36)
	at org.gradle.internal.execution.steps.BuildCacheStep.executeWithoutCache(BuildCacheStep.java:181)
	at org.gradle.internal.execution.steps.BuildCacheStep.lambda$execute$1(BuildCacheStep.java:71)
	at org.gradle.internal.Either$Right.fold(Either.java:175)
	at org.gradle.internal.execution.caching.CachingState.fold(CachingState.java:59)
	at org.gradle.internal.execution.steps.BuildCacheStep.execute(BuildCacheStep.java:69)
	at org.gradle.internal.execution.steps.BuildCacheStep.execute(BuildCacheStep.java:47)
	at org.gradle.internal.execution.steps.StoreExecutionStateStep.execute(StoreExecutionStateStep.java:36)
	at org.gradle.internal.execution.steps.StoreExecutionStateStep.execute(StoreExecutionStateStep.java:25)
	at org.gradle.internal.execution.steps.RecordOutputsStep.execute(RecordOutputsStep.java:36)
	at org.gradle.internal.execution.steps.RecordOutputsStep.execute(RecordOutputsStep.java:22)
	at org.gradle.internal.execution.steps.SkipUpToDateStep.executeBecause(SkipUpToDateStep.java:110)
	at org.gradle.internal.execution.steps.SkipUpToDateStep.lambda$execute$2(SkipUpToDateStep.java:56)
	at org.gradle.internal.execution.steps.SkipUpToDateStep.execute(SkipUpToDateStep.java:56)
	at org.gradle.internal.execution.steps.SkipUpToDateStep.execute(SkipUpToDateStep.java:38)
	at org.gradle.internal.execution.steps.ResolveChangesStep.execute(ResolveChangesStep.java:73)
	at org.gradle.internal.execution.steps.ResolveChangesStep.execute(ResolveChangesStep.java:44)
	at org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsFinishedStep.execute(MarkSnapshottingInputsFinishedStep.java:37)
	at org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsFinishedStep.execute(MarkSnapshottingInputsFinishedStep.java:27)
	at org.gradle.internal.execution.steps.ResolveCachingStateStep.execute(ResolveCachingStateStep.java:89)
	at org.gradle.internal.execution.steps.ResolveCachingStateStep.execute(ResolveCachingStateStep.java:50)
	at org.gradle.internal.execution.steps.ValidateStep.execute(ValidateStep.java:114)
	at org.gradle.internal.execution.steps.ValidateStep.execute(ValidateStep.java:57)
	at org.gradle.internal.execution.steps.CaptureStateBeforeExecutionStep.execute(CaptureStateBeforeExecutionStep.java:76)
	at org.gradle.internal.execution.steps.CaptureStateBeforeExecutionStep.execute(CaptureStateBeforeExecutionStep.java:50)
	at org.gradle.internal.execution.steps.SkipEmptyWorkStep.executeWithNoEmptySources(SkipEmptyWorkStep.java:254)
	at org.gradle.internal.execution.steps.SkipEmptyWorkStep.execute(SkipEmptyWorkStep.java:91)
	at org.gradle.internal.execution.steps.SkipEmptyWorkStep.execute(SkipEmptyWorkStep.java:56)
	at org.gradle.internal.execution.steps.RemoveUntrackedExecutionStateStep.execute(RemoveUntrackedExecutionStateStep.java:32)
	at org.gradle.internal.execution.steps.RemoveUntrackedExecutionStateStep.execute(RemoveUntrackedExecutionStateStep.java:21)
	at org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsStartedStep.execute(MarkSnapshottingInputsStartedStep.java:38)
	at org.gradle.internal.execution.steps.LoadPreviousExecutionStateStep.execute(LoadPreviousExecutionStateStep.java:43)
	at org.gradle.internal.execution.steps.LoadPreviousExecutionStateStep.execute(LoadPreviousExecutionStateStep.java:31)
	at org.gradle.internal.execution.steps.AssignWorkspaceStep.lambda$execute$0(AssignWorkspaceStep.java:40)
	at org.gradle.api.internal.tasks.execution.TaskExecution$4.withWorkspace(TaskExecution.java:281)
	at org.gradle.internal.execution.steps.AssignWorkspaceStep.execute(AssignWorkspaceStep.java:40)
	at org.gradle.internal.execution.steps.AssignWorkspaceStep.execute(AssignWorkspaceStep.java:30)
	at org.gradle.internal.execution.steps.IdentityCacheStep.execute(IdentityCacheStep.java:37)
	at org.gradle.internal.execution.steps.IdentityCacheStep.execute(IdentityCacheStep.java:27)
	at org.gradle.internal.execution.steps.IdentifyStep.execute(IdentifyStep.java:44)
	at org.gradle.internal.execution.steps.IdentifyStep.execute(IdentifyStep.java:33)
	at org.gradle.internal.execution.impl.DefaultExecutionEngine$1.execute(DefaultExecutionEngine.java:76)
	at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeIfValid(ExecuteActionsTaskExecuter.java:139)
	at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.execute(ExecuteActionsTaskExecuter.java:128)
	at org.gradle.api.internal.tasks.execution.CleanupStaleOutputsExecuter.execute(CleanupStaleOutputsExecuter.java:77)
	at org.gradle.api.internal.tasks.execution.FinalizePropertiesTaskExecuter.execute(FinalizePropertiesTaskExecuter.java:46)
	at org.gradle.api.internal.tasks.execution.ResolveTaskExecutionModeExecuter.execute(ResolveTaskExecutionModeExecuter.java:51)
	at org.gradle.api.internal.tasks.execution.SkipTaskWithNoActionsExecuter.execute(SkipTaskWithNoActionsExecuter.java:57)
	at org.gradle.api.internal.tasks.execution.SkipOnlyIfTaskExecuter.execute(SkipOnlyIfTaskExecuter.java:56)
	at org.gradle.api.internal.tasks.execution.CatchExceptionTaskExecuter.execute(CatchExceptionTaskExecuter.java:36)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.executeTask(EventFiringTaskExecuter.java:77)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:55)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:52)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:204)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:199)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:53)
	at org.gradle.internal.operations.DefaultBuildOperationExecutor.call(DefaultBuildOperationExecutor.java:73)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter.execute(EventFiringTaskExecuter.java:52)
	at org.gradle.execution.plan.LocalTaskNodeExecutor.execute(LocalTaskNodeExecutor.java:69)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:327)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:314)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:307)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:293)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.execute(DefaultPlanExecutor.java:417)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.run(DefaultPlanExecutor.java:339)
	at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
	at org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:48)
Caused by: java.lang.reflect.InvocationTargetException
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at org.jetbrains.kotlin.gradle.internal.KaptExecution.run(KaptWithoutKotlincTask.kt:320)
	at org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask$KaptExecutionWorkAction.execute(KaptWithoutKotlincTask.kt:266)
	at org.gradle.workers.internal.DefaultWorkerServer.execute(DefaultWorkerServer.java:63)
	at org.gradle.workers.internal.NoIsolationWorkerFactory$1$1.create(NoIsolationWorkerFactory.java:66)
	at org.gradle.workers.internal.NoIsolationWorkerFactory$1$1.create(NoIsolationWorkerFactory.java:62)
	at org.gradle.internal.classloader.ClassLoaderUtils.executeInClassloader(ClassLoaderUtils.java:100)
	at org.gradle.workers.internal.NoIsolationWorkerFactory$1.lambda$execute$0(NoIsolationWorkerFactory.java:62)
	at org.gradle.workers.internal.AbstractWorker$1.call(AbstractWorker.java:44)
	at org.gradle.workers.internal.AbstractWorker$1.call(AbstractWorker.java:41)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:204)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:199)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:53)
	at org.gradle.internal.operations.DefaultBuildOperationExecutor.call(DefaultBuildOperationExecutor.java:73)
	at org.gradle.workers.internal.AbstractWorker.executeWrappedInBuildOperation(AbstractWorker.java:41)
	at org.gradle.workers.internal.NoIsolationWorkerFactory$1.execute(NoIsolationWorkerFactory.java:59)
	at org.gradle.workers.internal.DefaultWorkerExecutor.lambda$submitWork$2(DefaultWorkerExecutor.java:205)
	at org.gradle.internal.work.DefaultConditionalExecutionQueue$ExecutionRunner.runExecution(DefaultConditionalExecutionQueue.java:187)
	at org.gradle.internal.work.DefaultConditionalExecutionQueue$ExecutionRunner.access$700(DefaultConditionalExecutionQueue.java:120)
	at org.gradle.internal.work.DefaultConditionalExecutionQueue$ExecutionRunner$1.run(DefaultConditionalExecutionQueue.java:162)
	at org.gradle.internal.Factories$1.create(Factories.java:31)
	at org.gradle.internal.work.DefaultWorkerLeaseService.withLocks(DefaultWorkerLeaseService.java:249)
	at org.gradle.internal.work.DefaultWorkerLeaseService.runAsWorkerThread(DefaultWorkerLeaseService.java:109)
	at org.gradle.internal.work.DefaultWorkerLeaseService.runAsWorkerThread(DefaultWorkerLeaseService.java:114)
	at org.gradle.internal.work.DefaultConditionalExecutionQueue$ExecutionRunner.runBatch(DefaultConditionalExecutionQueue.java:157)
	at org.gradle.internal.work.DefaultConditionalExecutionQueue$ExecutionRunner.run(DefaultConditionalExecutionQueue.java:126)
	... 2 more
Caused by: java.lang.NullPointerException: processingEnv must not be null
	at androidx.room.compiler.processing.javac.JavacBasicAnnotationProcessor$xEnv$2.invoke(JavacBasicAnnotationProcessor.kt:38)
	at androidx.room.compiler.processing.javac.JavacBasicAnnotationProcessor$xEnv$2.invoke(JavacBasicAnnotationProcessor.kt:37)
	at kotlin.SynchronizedLazyImpl.getValue(LazyJVM.kt:74)
	at androidx.room.compiler.processing.javac.JavacBasicAnnotationProcessor.getXEnv(JavacBasicAnnotationProcessor.kt:37)
	at androidx.room.compiler.processing.javac.JavacBasicAnnotationProcessor.getXProcessingEnv(JavacBasicAnnotationProcessor.kt:47)
	at androidx.room.RoomProcessor.getSupportedOptions(RoomProcessor.kt:53)
	at org.jetbrains.kotlin.kapt3.base.incremental.IncrementalProcessor.getSupportedOptions(incrementalProcessors.kt)
	at org.jetbrains.kotlin.kapt3.base.incremental.IncrementalProcessor.createDependencyCollector(incrementalProcessors.kt:49)
	at org.jetbrains.kotlin.kapt3.base.incremental.IncrementalProcessor.access$createDependencyCollector(incrementalProcessors.kt:26)
	at org.jetbrains.kotlin.kapt3.base.incremental.IncrementalProcessor$dependencyCollector$1.invoke(incrementalProcessors.kt:29)
	at org.jetbrains.kotlin.kapt3.base.incremental.IncrementalProcessor$dependencyCollector$1.invoke(incrementalProcessors.kt:29)
	at kotlin.SynchronizedLazyImpl.getValue(LazyJVM.kt:74)
	at org.jetbrains.kotlin.kapt3.base.incremental.IncrementalProcessor.getRuntimeType(incrementalProcessors.kt:84)
	at org.jetbrains.kotlin.kapt3.base.incremental.IncrementalAptCache.updateCache(IncrementalAptCache.kt:30)
	at org.jetbrains.kotlin.kapt3.base.incremental.JavaClassCacheManager.updateCache(cache.kt:23)
	at org.jetbrains.kotlin.kapt3.base.AnnotationProcessingKt.doAnnotationProcessing(annotationProcessing.kt:102)
	at org.jetbrains.kotlin.kapt3.base.AnnotationProcessingKt.doAnnotationProcessing$default(annotationProcessing.kt:33)
	at org.jetbrains.kotlin.kapt3.base.Kapt.kapt(Kapt.kt:47)
	... 34 more


* Get more help at https://help.gradle.org

BUILD FAILED in 1m 47s
Please consult deprecation warnings for more details.
49 actionable tasks: 49 executed
Error: Process completed with exit code 1.
