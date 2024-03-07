# SmartMusic
An application for music lovers using AI to pickup songs based on your own experiences

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
<ul>
    <li>Clone the repository</li>
    <li>Go to <a href="https://developer.spotify.com/dashboard/applications">Spotify Developer Dashboard</a> and create a new application as described <a href="https://developer.spotify.com/documentation/general/guides/app-settings/">here</a></li>
    <li>Create a new folder called 'raw' in the resources (src/main/res) folder</li>
    <li>Create a new file called 'spotify_config.properties' in the 'raw' folder</li>
    <li>In the 'spotify_config.properties' file, add the following lines:
    ```
    spotify_client_id=YOUR_CLIENT_ID(From Spotify Developer Dashboard)
    spotify_client_secret=YOUR_CLIENT_SECRET(From Spotify Developer Dashboard)
    spotify_redirect_uri=YOUR_REDIRECT_URI(From Spotify Developer Dashboard)
    ```
    </li>
    <li>Run the application</li>
* if you run into any issues, please let me know
</ul>