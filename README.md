# Instuctions : 
1. Create a firebase account, install the firebase plugin in Android Studio.
2. In firebase console (`https://console.firebase.google.com/u/0/`), create a new firebase project.
3. Add Authentication, Firestore Database, and Storage from the left hand side `Product categories` panel's `Build` section.
4. In Authentication, go to `Sign-in method`, and add Email/Password as a provider.
5. Click on the settings icon besides Project Overview on the top left, then go to Project settings.
   - Scroll down and download the google-services.json file.
   - Paste this file in your Android project's root/app directory.
   - Keep this file safe, and confidential.
6. I dont' remember it will, but there was a need to find my laptop's SHA-1 and SHA-256 signatures.
   - There was a command to find these signatures, use chatpgpt to find this command.
   - Paste the obtained signatures in the bottom most section - `SHA certificate fingerprints` of your firebase project settings.
#
# Features :
1. Has two different types of accounts - student account and college admin account.
2. Students can :
   - Signup by entering their details and uploading required documents.
   - Login/logout.
   - Apply for any course by selecting any degree and then selecting any college that offers that degree.
   - Student can also view their application status.
3. College admins can :
   - Signup by entering college details and uploading UGC approval certificate.
   - Login/logout.
   - Ad degrees that are offered by his college.
   - View the list of students who have applied for each degree.
   - View the details of each student who has applied as well download his documents on the college admin's phone.
   - Accepting any application will notify that student via email.
