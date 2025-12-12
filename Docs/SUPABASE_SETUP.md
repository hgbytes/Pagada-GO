# Supabase Authentication Setup for Pagada

## Prerequisites
1. A Supabase account (sign up at https://supabase.com)
2. Android Studio with the project opened

## Step 1: Create a Supabase Project

1. Go to https://supabase.com and sign in
2. Click on "New Project"
3. Fill in your project details:
   - **Name**: Pagada
   - **Database Password**: Choose a strong password
   - **Region**: Select the closest region to your users
4. Wait for the project to be created (takes about 2 minutes)

## Step 2: Get Your Supabase Credentials

1. Once your project is created, go to **Settings** → **API**
2. Copy the following values:
   - **Project URL** (under "Project URL")
   - **anon/public key** (under "Project API keys")

## Step 3: Configure the App

⚠️ **IMPORTANT: Never hardcode credentials in your source code!**

1. Open the file: `local.properties` (in your project root)
2. Add your Supabase credentials:
   ```properties
   supabase.url=YOUR_SUPABASE_PROJECT_URL
   supabase.anon.key=YOUR_SUPABASE_ANON_KEY
   ```
3. Replace `YOUR_SUPABASE_PROJECT_URL` with your Project URL from Step 2
4. Replace `YOUR_SUPABASE_ANON_KEY` with your anon/public key from Step 2
5. Save the file

**Example:**
```properties
supabase.url=https://abcdefghijk.supabase.co
supabase.anon.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

6. **Sync Gradle**: Click "Sync Now" in Android Studio or go to File → Sync Project with Gradle Files

The app is configured to read these values securely from `local.properties` using BuildConfig. 
✅ These credentials are automatically excluded from Git by `.gitignore`
📖 See [SECURITY.md](SECURITY.md) for detailed security best practices

## Step 4: Enable Email Authentication

1. In your Supabase dashboard, go to **Authentication** → **Providers**
2. Ensure **Email** provider is enabled
3. Configure email templates if needed (optional)

## Step 5: Configure Email Settings (Optional)

By default, Supabase sends confirmation emails. You can:

### Option A: Disable Email Confirmation (for development)
1. Go to **Authentication** → **Settings**
2. Under **Email Auth**, disable "Enable email confirmations"
3. This allows users to sign in immediately without email verification

### Option B: Configure Custom SMTP (for production)
1. Go to **Settings** → **Auth** → **SMTP Settings**
2. Configure your SMTP provider (SendGrid, Mailgun, etc.)

## Step 6: Test the Authentication

1. Run the app in Android Studio
2. Try to register a new account:
   - Name: Test User
   - Email: test@example.com
   - Password: Test123!@#
3. Verify the account is created in Supabase:
   - Go to **Authentication** → **Users** in your Supabase dashboard
   - You should see the new user

## Step 7: Database Schema (Optional)

If you want to store additional user data (like profile information), create a table:

```sql
-- Create a profiles table
create table profiles (
  id uuid references auth.users on delete cascade primary key,
  name text,
  avatar_url text,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Enable Row Level Security
alter table profiles enable row level security;

-- Create policies
create policy "Users can view their own profile"
  on profiles for select
  using (auth.uid() = id);

create policy "Users can update their own profile"
  on profiles for update
  using (auth.uid() = id);

-- Create a trigger to create profile on signup
create or replace function public.handle_new_user()
returns trigger as $$
begin
  insert into public.profiles (id, name)
  values (new.id, new.raw_user_meta_data->>'name');
  return new;
end;
$$ language plpgsql security definer;

create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();
```

## Troubleshooting

### Issue: "Invalid API key"
- **Solution**: Double-check that you copied the correct anon/public key from Supabase

### Issue: "Network error"
- **Solution**: Ensure you have internet permission in AndroidManifest.xml:
  ```xml
  <uses-permission android:name="android.permission.INTERNET" />
  ```

### Issue: "Email not confirmed"
- **Solution**: Either disable email confirmation in Supabase settings or check your email for the confirmation link

### Issue: Build errors
- **Solution**: Sync Gradle files and rebuild the project

## Security Best Practices

1. **Never commit your Supabase credentials to Git**:
   - Use environment variables or local.properties
   - Add the configuration file to .gitignore

2. **Example using BuildConfig**:
   ```kotlin
   // In app/build.gradle.kts
   android {
       defaultConfig {
           buildConfigField("String", "SUPABASE_URL", "\"${project.findProperty("SUPABASE_URL")}\"")
           buildConfigField("String", "SUPABASE_KEY", "\"${project.findProperty("SUPABASE_KEY")}\"")
       }
   }
   
   // In local.properties
   SUPABASE_URL=your_url_here
   SUPABASE_KEY=your_key_here
   
   // In SupabaseClient.kt
   val client = createSupabaseClient(
       supabaseUrl = BuildConfig.SUPABASE_URL,
       supabaseKey = BuildConfig.SUPABASE_KEY
   ) {
       install(Auth)
       install(Postgrest)
   }
   ```

## Next Steps

1. Implement password reset functionality
2. Add social authentication (Google, Facebook, etc.)
3. Implement user profile management
4. Add role-based access control

## Resources

- [Supabase Documentation](https://supabase.com/docs)
- [Supabase Auth Documentation](https://supabase.com/docs/guides/auth)
- [Kotlin Supabase Client](https://github.com/supabase-community/supabase-kt)

