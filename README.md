# PickPhotoView

[![](https://jitpack.io/v/bardss/PickPhotoView.svg)](https://jitpack.io/#bardss/PickPhotoView)

PickPhotoView handles:
- camera and files permission
- enables user to choose photo from camera or gallery
- enables user to pick multiple or one photo
- compress picture quality
- also enables only to show photos from paths (without pick)

Photos from camera are saved to app directory.

Right now library works only with API less than 31.
Still uses `requestLegacyExternalStorage`

## Demo

Mode: `ENABLE_ADD_MULTIPLE`

![Alt Text](https://s9.gifyu.com/images/ezgif.com-gif-maker5.gif)

available also in Mode: `ENABLE_ADD_ONE` and Mode: `ONLY_SHOW`

## Usage

Add to Android Manifest:
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
```

And within <application/>
```xml
android:requestLegacyExternalStorage="true"
```

Add view in  XML:
```xml
<com.jakubaniola.pickphotoview.PickPhotoLayout
    android:id="@+id/pick_photo_layout"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="100dp"
    app:mode="ENABLE_ADD_MULTIPLE"
    app:placeholderPicture="@drawable/ic_picture" // optional
    app:imageCompressQuality="80" // optional
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintTop_toTopOf="parent"/>
 ```

In add mode it is obligatory to add those lines to fragment/activity:
```kotlin
class MainActivity : AppCompatActivity(), PickPhotoActions {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<PickPhotoLayout>(R.id.pick_photo_layout).setPickPhotoFragment(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        findViewById<PickPhotoLayout>(R.id.pick_photo_layout).onPicturePicked(requestCode, resultCode, data)
    }
}
```

If using mode `ONLY_SHOW`, you have to provide paths to files:
```kotlin
findViewById<PickPhotoLayout>(R.id.pick_photo_layout).setPictures(paths)
```

## Dependency

Add the following lines in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency
```
dependencies {
    implementation 'com.github.bardss:PaintableVectorView:CURRENT_VERSION'
}
```

## License

```
Copyright 2021 Jakub Aniola

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
