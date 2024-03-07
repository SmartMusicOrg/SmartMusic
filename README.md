# SmartMusic
An application for music lovers using AI to pick up songs based on your own experiences
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

## Getting Started
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
</ul>
