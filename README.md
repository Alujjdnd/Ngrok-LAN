![Ngrok LAN Banner](https://github.com/Alujjdnd/Ngrok-LAN/blob/1.18.2/docs/assets/banner.png?raw=true)

<h1 align="center">
  Ngrok LAN
</h1>

<div align="center">
  <img alt="License" src=https://img.shields.io/github/license/Alujjdnd/Ngrok-LAN>
  <img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/Alujjdnd/Ngrok-LAN">
  <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/Alujjdnd/Ngrok-LAN">
  <a href="https://www.codacy.com/gh/Alujjdnd/Ngrok-LAN/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Alujjdnd/Ngrok-LAN&amp;utm_campaign=Badge_Grade"><img src="https://app.codacy.com/project/badge/Grade/b38f7148f2934ad8b6d4413b70a173fd"/></a>
  <img alt="GitHub commit activity" src="https://img.shields.io/github/commit-activity/m/alujjdnd/ngrok-lan">
  </br>
  <img src=https://raw.githubusercontent.com/Alujjdnd/Ngrok-LAN/1.18.2/docs/assets/OSSrs.png>
</div>

## Help Needed!
We are trying to port this mod to QuiltMC, many thanks to those who have contributed already! However, we are hope that a Quilt specific config library will become available to use soon, since our mod requires editing a config file we want the process to be as easy as possible for our users.

## Introduction
Ngrok LAN is a Fabric mod that enables you to share your singleplayer world with your friends across the globe, all via
clicking on the new "Open Public Server" button! This mod uses Ngrok, a free service to ease the creation of servers, as
the main service to "tunnel" your connection to the internet, and will generate a link for your friends to connect to
after you click on "Open Public Server".

For more on how Ngrok-LAN works, check out [What is Ngrok](#what-is-ngrok).

<div align="center">
  <img src="https://raw.githubusercontent.com/Alujjdnd/Ngrok-LAN/1.18.2/docs/assets/demo.gif" alt="Network Diagram" width="70%"/>
</div>

# Installation/Quick Start

## Dependencies

The mod requires the following mods:

- [Fabric Loader & Fabric API](https://fabricmc.net/use/installer/)
- [ClothConfig API](https://www.curseforge.com/minecraft/mc-mods/cloth-config)
- [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu)

## Install Fabric Loader and Ngrok-LAN

1. Go to the [FabricMC website](https://fabricmc.net/use/installer/) and download the latest installer
2. Run the JAR, a window will pop up. Choose the correct version of Minecraft and loader (this mod currently requires
   Minecraft v1.18.2 and loader v0.13.3), and click on "Install".
3. A new game version and profile will be created in the launcher's menu, which you can now use to launch Fabric.
4. Put the mod JAR (available under [Releases](https://github.com/Alujjdnd/Ngrok-LAN/releases)) into your "mods" folder,
   along with the ClothConfig API and ModMenu JARs. (The Fabric API JAR should already be in your mods folder from the
   previous section)
5. Run the newly installed profile from step 3, and Ngrok LAN should load

## Configuring Ngrok-LAN

| **Field**                   | **Description**                                              |
|-----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| **"Enabled?"**              | This will enable/disable the mod, so you can configure whether the world is shared with Ngrok                                              |
| **"Ngrok Auth Token"**      | This is the field to input your Ngrok auth token, see [getting your auth token](#getting-your-ngrok-auth-token)                            |
| **"Region"**                | This is where you can select the region of the Ngrok datacenter that you wish to use, use ones that are closest to you to minimise latency |
| **"Require Authentication"**| Ticking this box will start the server in online mode. If this box is unchecked, the server will be started in offline mode. This is mainly for when the other players are using cracked Minecraft accounts. |
| **"Spawn Protection Radius"**| Prevents non-opped players from breaking and placing blocks within `X` blocks of spawn |

### Ngrok Regions

| Code | Region |
|----|---------------|
| US | United States |
| EU | Europe        |
| AP | Asia/Pacific  |
| AU | Australia     |
| SA | South America |
| JP | Japan         |
| IN | India         |

## Starting Ngrok LAN

1. Make sure that Ngrok LAN is enabled, the auth token is valid, and the correct region is selected
2. Open a new singleplayer world, and click on Open to LAN
3. Configure the game mode and whether to allow cheats
4. Press on "Open Public Server"
5. The message "Ngrok service initiated successfully" should appear in chat, with a link highlighted in yellow
    - If not, check [common issues](#common-issues) below
6. The link is automatically copied to your clipboard, and can now be shared with your friends
7. Other people just need to put this as a server IP into their multiplayer list, and join the world

## Getting your Ngrok Auth Token

1. Go to https://ngrok.com/, and sign up for a new free account
2. On the dashboard, on your left-hand side menu, click on Getting Started > Your Auth Token
3. Copy the auth token, and put this into Ngrok LAN _(don't share this token with other people)_

## Commands
| **Command**        | **Description**                                      |
|--------------------|------------------------------------------------------|
| `/op <username>`   | Gives the specified player server operator status.    |
| `/deop <username>` | Removes server operator status from specified player. |
| `/whitelist add <username>` | Adds the player profile(s) to the whitelist. The player does not need to be online. |
| `/whitelist on` | Enables the server's use of a whitelist. |
| `/whitelist off` | Disables the server's use of a whitelist. |
| `/whitelist remove` | Removes the player profile(s) from the whitelist. The player does not need to be online. |
| `/whitelist list` | Displays all player profiles in the whitelist. |
| `/whitelist reload <username>` | Reloads the list of player profiles in whitelist.json from disk (used when whitelist.json has been modified outside of Minecraft). |

# Common Issues

| **Error Message**                                                                    | **Resolution**                                                                                                                                             |
|--------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| An attempt was made to access a socket in a way forbidden by its access permissions. | Restart "Host Network Service" under the "Services" program                                                                                                |
| The authtoken you specified does not look like a proper ngrok tunnel (ERR_NGROK_105) | Check that your auth token is valid, and is correctly inputted into the mod config                                                                         |
| Your account is limited to <NUMBER> simultaneous ngrok agent session...              | Check that you are not running any other tunnels with this auth token, try change to regions if you are still getting this error                           |

- For more errors, error codes and their resolution, check the [Ngrok docs](https://ngrok.com/docs/errors)

## Other Issues

Please open a new issue in [Issues](https://github.com/Alujjdnd/Ngrok-LAN/issues)

# What is Ngrok?

Ngrok is a **free** service that exposes your applications running locally to the internet, without port forwarding.
Usually, when setting up a Minecraft server, you would have to open the server, and "forward" your port through your
router. Port forwarding "publishes" your server to the internet, so anyone with you IP can join. Think of this as your
router "forwarding" the network packets from your server (part of your local network) to your friends (part of the wider
internet).

Using Ngrok, however, we don't need to forward the port. All the internet traffic is routed through an application (
Ngrok-LAN in this case), then their datacentres. Ngrok provides us with a link in return, that we can give to our
friends to connect to our server. This doesn't require any other configuration on your side - as long as you have
internet, and an Ngrok account, you can host your singleplayer world to your friends across the globe.

<div align="center">
<img src="https://raw.githubusercontent.com/Alujjdnd/Ngrok-LAN/1.18.2/docs/assets/Network.jpg" alt="Network Diagram" width="80%"/>
</div>

## Ngrok Account Requirements

**The mod only requires a free Ngrok account**. The free tier for Ngrok allows you to have 1 free online tunnel
simultaenously, which means that, with one Ngrok free account, you can only host 1 Ngrok-LAN server at the same time.
The paid tiers allows you to have custom domains and more simultaenous online tunnels, but custom domains are not yet
supported in this mod.

More pricing info please refer to [Ngrok's website](https://ngrok.com/pricing)

## Known Incompatibilities
Please refer to [Incompatibilities](https://github.com/Alujjdnd/Ngrok-LAN/labels/incompatibility) to find a list of incompatible mods.
  </br>Currently, only Proximity Voice Chat mods are incompatible. This is unlikely to be fixed.
  
# TODO

- Port to QuiltMC (Please reach out and help us!)
- 1.17.1 Build

# License

This mod is available under the MIT license.
