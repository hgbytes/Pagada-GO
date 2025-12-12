# Security Best Practices - Supabase Integration

## ✅ Implemented Security Measures

### 1. Credentials Protection

#### ❌ NEVER DO THIS:
```kotlin
// DON'T hardcode credentials!
val client = createSupabaseClient(
    supabaseUrl = "https://myproject.supabase.co",  // ❌ Exposed!
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." // ❌ Exposed!
)
```

#### ✅ CORRECT APPROACH:
```kotlin
// DO use BuildConfig from local.properties
val client = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,     // ✅ Secure!
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY  // ✅ Secure!
)
```

### 2. File Structure

```
Pagada/
├── local.properties           # ✅ In .gitignore - NEVER commit this
├── local.properties.example   # ✅ Template for other developers
├── .gitignore                 # ✅ Contains local.properties
└── app/
    ├── build.gradle.kts       # ✅ Reads from local.properties
    └── src/main/java/...
        └── data/
            └── SupabaseClient.kt  # ✅ Uses BuildConfig
```

## 📝 Setup Instructions

### Step 1: Configure local.properties

1. Open `local.properties` in your project root
2. Add your Supabase credentials:

```properties
# Get these from: https://app.supabase.com/project/_/settings/api
supabase.url=https://your-project-id.supabase.co
supabase.anon.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Step 2: Sync Gradle

After adding credentials to `local.properties`:
1. Click **File** → **Sync Project with Gradle Files**
2. Or click the Gradle sync icon in the toolbar

### Step 3: Verify

Build the project to ensure BuildConfig is generated:
```bash
./gradlew clean build
```

## 🔒 Security Checklist

- [x] **local.properties is in .gitignore** - Prevents accidental commits
- [x] **BuildConfig is used** - Credentials loaded at build time
- [x] **Template file provided** - local.properties.example for team members
- [x] **No hardcoded secrets** - All sensitive data externalized
- [x] **Documentation created** - Clear instructions for setup

## 🚨 Important Security Notes

### The Anon Key is Public-Facing
- ✅ **Safe to use in Android app** - It's designed for client-side use
- ✅ **Protected by RLS** - Row Level Security policies protect your data
- ❌ **DON'T use service_role key** - NEVER put this in your app!

### What if Credentials Leak?

If you accidentally commit credentials to Git:

1. **Rotate your keys immediately** in Supabase Dashboard:
   - Go to Settings → API
   - Click "Reset anon/public key"

2. **Remove from Git history**:
   ```bash
   # Use git-filter-repo or BFG Repo-Cleaner
   git filter-branch --force --index-filter \
     "git rm --cached --ignore-unmatch local.properties" \
     --prune-empty --tag-name-filter cat -- --all
   ```

3. **Force push** (⚠️ Coordinate with team first):
   ```bash
   git push origin --force --all
   ```

## 🎯 Advanced: Using Hilt for Dependency Injection

For larger apps, consider using Hilt for better testability and architecture.

### Setup Hilt (Optional)

1. **Add Hilt dependencies** to `build.gradle.kts`:

```kotlin
plugins {
    // ...
    id("com.google.dagger.hilt.android") version "2.51.1"
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}

dependencies {
    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
}
```

2. **Create Application class**:

```kotlin
package com.pagadasports.pagada

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PagadaApplication : Application()
```

3. **Update AndroidManifest.xml**:

```xml
<application
    android:name=".PagadaApplication"
    ...>
```

4. **Use SupabaseModule.kt** (already created for you!)

5. **Inject into ViewModels**:

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {
    // Use supabase client here
}
```

## 📚 Additional Resources

- [Supabase Security Best Practices](https://supabase.com/docs/guides/auth/managing-user-data#security-best-practices)
- [Android App Security](https://developer.android.com/topic/security/best-practices)
- [Dagger Hilt Documentation](https://dagger.dev/hilt/)

## ⚡ Quick Reference

### Get Supabase Credentials
1. Go to [Supabase Dashboard](https://app.supabase.com)
2. Select your project
3. Go to **Settings** → **API**
4. Copy:
   - **Project URL** → `supabase.url`
   - **anon/public key** → `supabase.anon.key`

### Troubleshooting

**Error: "SUPABASE_URL is not defined"**
- Solution: Add credentials to `local.properties` and sync Gradle

**Error: "BuildConfig cannot be resolved"**
- Solution: Enable `buildConfig = true` in `build.gradle.kts` (already done!)

**Credentials not loading**
- Solution: Clean and rebuild: `./gradlew clean build`

---

## 🎉 You're All Set!

Your Supabase credentials are now securely configured and protected from accidental exposure. Happy coding!

