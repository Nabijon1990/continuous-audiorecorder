# Continuous audiorecorder [ ![Download](https://api.bintray.com/packages/lassana/maven/continuous-audiorecorder/images/download.svg) ](https://bintray.com/lassana/maven/continuous-audiorecorder/_latestVersion)

Implementation of the missing feature in Android SDK: audio recorder with a pause. Based on [mp4parser](https://code.google.com/p/mp4parser/).

## Import

Grab the latest version from Bintray:

    repositories {
        jcenter()
    }
    dependencies {
        implementation "com.googlecode.mp4parser:isoparser:1.0.2"
        implementation "com.github.lassana:continuous-audiorecorder:1.3.2"
    }

or just copy the `recorder` module into your project and add a new dependency:

    dependencies {
        implementation project(':recorder')
    }

## Usage

Start recording:

    AudioRecorder recorder = AudioRecorderBuilder.with(context)
                                                 .fileName(filename)
                                                 .config(AudioRecorder.MediaRecorderConfig.DEFAULT)
                                                 .loggable()
                                                 .build();

    recorder.start(new AudioRecorder.OnStartListener() {
        @Override
        public void onStarted() {
            // started
        }

        @Override
        public void onException(Exception e) {
            // error
        }
    });

Pause:

    mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
        @Override
        public void onPaused(String activeRecordFileName) {
            // paused
        }

        @Override
        public void onException(Exception e) {
            // error
        }
    });

The `start()` method continues existing record also.

## [License](https://github.com/lassana/continuous-audiorecorder/blob/master/LICENSE)

This project is licensed under the FreeBSD License.
