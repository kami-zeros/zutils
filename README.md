Step 1. Add the JitPack repository to your build file.
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.kami-zeros:zutils:1.0.6'
	}

# zutils
 - 1、ZLog：日志打印。
 - 2、ToastUtil：吐司。
 - 3、PermissionUtils：权限管理。
 - 4、AppManager：Activity管理。
 - 5、DeviceInfoUtils：设备信息。
 - 6、DensityUtil：单位，屏幕，隐藏软键盘。