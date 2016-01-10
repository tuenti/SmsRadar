This is a fork of SmsRadar (https://github.com/tuenti/SmsRadar). The aim is to make the project work with android studio and resolve any problems that it has with new versions of android. See the original Readme file for more information on how to use the library. The functionality should not be changed.

## Gradle repository to use this fork
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}

## Dependency to use this fork

```groovie
dependencies{
    compile 'org.vaslabs.smsradar:smsradar-library:2.0.0'
}
```

## Declare SmsRadar service in your AndroidManifest:

```xml
<service
        android:name="org.vaslabs.smsradar.SmsRadarService"
        android:exported="false"
        android:label="@string/app_name"/>
```
