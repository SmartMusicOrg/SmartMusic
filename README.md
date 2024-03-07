# SmartMusic
An application for music lovers using AI to pick up songs based on your own experiences
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#versioning">Versioning</a></li>
    <li><a href="#using-spotify-in-android">Using Spotify In Android</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

## Getting Started
### Prerequisites
To use this application, you will need to have to have spotify installed on your device and have a spotify account. 
To run the code as a developer, you will need the following:
<ul>
    <li>Android Studio</li>
    <li>Spotify Developer Account</li>
</ul><br>
<a href="#smartmusic">Back to top</a>

### Installation
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
<ul>
    <li>Clone the repository</li>
    <li>Go to <a href="https://developer.spotify.com/dashboard/applications">Spotify Developer Dashboard</a> and create a new application as described <a href="https://developer.spotify.com/documentation/web-api/concepts/apps">here</a></li>
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
</ul><br>
<a href="#smartmusic">Back to top</a>

## Versioning
This project build in Android Studio Iguana | 2023.2.1 , AGP(gradle) 8.3.0 and use the following dependencies:
<ul>
    <li><a href = "https://search.maven.org/artifact/org.immutables/gson/2.10.1/jar">Gson Package</a></li>
    <li><a href = "https://search.maven.org/artifact/com.android.volley/volley/1.2.1/aar">Volley Package</a></li>
    <li><a href = "https://search.maven.org/artifact/com.spotify.android/auth/2.1.0/aar">Spotify Auth Package</a></li>
</ul><br>
<a href="#smartmusic">Back to top</a>

## Using Spotify In Android
Coming soon...<br>
<a href="#smartmusic">Back to top</a>

## Contact
If you have any questions, please feel free to contact us at finalprojectmanager@gmail.com.<br>
<a href="#smartmusic">Back to top</a>

