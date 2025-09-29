from pathlib import Path
path=Path('gradle/libs.versions.toml')
text=path.read_text()
old="firebase-messaging = { group = \"com.google.firebase\", name = \"firebase-messaging-ktx\" }"
if old in text and 'firebase-analytics = { group = "com.google.firebase", name = "firebase-analytics-ktx" }' not in text:
    text=text.replace(old, old+"\nfirebase-analytics = { group = \"com.google.firebase\", name = \"firebase-analytics-ktx\" }\nfirebase-crashlytics = { group = \"com.google.firebase\", name = \"firebase-crashlytics-ktx\" }")
section='# Firebase\nfirebaseBom = "33.6.0"'
if section in text and 'firebaseCrashlyticsPlugin' not in text:
    text=text.replace(section, section+"\nfirebaseCrashlyticsPlugin = \"2.9.9\"")
plugin_line="google-services = { id = \"com.google.gms.google-services\", version = \"4.4.2\" }"
if plugin_line in text and 'firebaseCrashlytics = { id = "com.google.firebase.crashlytics"' not in text:
    text=text.replace(plugin_line, plugin_line+"\nfirebaseCrashlytics = { id = \"com.google.firebase.crashlytics\", version.ref = \"firebaseCrashlyticsPlugin\" }")
path.write_text(text)
