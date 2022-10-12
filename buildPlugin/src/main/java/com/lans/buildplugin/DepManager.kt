/**
 * 具体依赖内容
 *
 */
object DepManager {

    /**
     * 依赖版本
     */
    private const val core_ktx_version = "1.7.0"
    private const val appcompat_version = "1.5.1"
    private const val material_version = "1.6.1"
    private const val constraintlayout_version = "2.1.4"

    const val core = "androidx.core:core-ktx:$core_ktx_version"

    const val appcompat = "androidx.appcompat:appcompat:$appcompat_version"
    const val material = "com.google.android.material:material:$material_version"
    const val constraintlayout =
        "androidx.constraintlayout:constraintlayout:$constraintlayout_version"

}

