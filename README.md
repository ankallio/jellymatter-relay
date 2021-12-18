# jellymatter-relay

Updates Mattermost user account's custom status to show "Currently playing" information from Jellyfin.

Jellyfin's Webhook plugin can be used to send information about playback, but it only supports POST calls. Mattermost API expects PUT and DELETE calls to update the custom status. This service translates webhooks to API calls.

## Install service

Build a WAR file with `mvn install`. It can be run directly with `java -jar target/jellymatter.war` or it can be deployed to a Tomcat 9 service.

## Set up Mattermost

Create a Personal Access Token: Account Settings / Security / Personal Access Tokens.

## Set up Jellyfin

* Go to Admin Dashboard
* Open Advanced / Plugins
* Open Catalog
* Install "Webhook" plugin
* Open Webhook configuration view
* Add "Generic Destination"
* Enter any name to Webhook Name
* Enter URL of customstatus endpoint to Webhook Url. For example `https://app.example.com/jellymatter/customstatus`
* Select Notification Types: Playback Start, Playback Stop
* Select your account only in User Filter
* Select Item type: Songs
* Enter template, change the access token to yours:

```
{
"access_token":"ENTER_YOUR_MATTERMOST_ACCESS_TOKEN",
"emoji":"musical_note",
"text":"{{{Artist}}} - {{{Name}}}",
"run_time_ticks":{{RunTimeTicks}},
"playback_position_ticks":{{PlaybackPositionTicks}},
"notification_type":"{{{NotificationType}}}"
}
```
* Click Save
