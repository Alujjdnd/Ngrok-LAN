![Ngrok LAN Banner](https://github.com/Alujjdnd/Ngrok-LAN/blob/main/docs/assets/banner.png?raw=true)

<h1 align="center">
  Ngrok LAN
</h1>

<div align="center">
  <img src=https://img.shields.io/github/license/Alujjdnd/Ngrok-Lan>
  <img src=https://img.shields.io/github/v/release/Alujjdnd/Ngrok-Lan>
  </br>
  <img src=https://raw.githubusercontent.com/Alujjdnd/Ngrok-LAN/main/docs/assets/AGPL-v3.png>
</div>

Ngrok LAN is a Fabric mod that enables you to share your singleplayer world with your friends across the globe, all via clicking on "Open to LAN". This mod uses Ngrok as the main service to "tunnel" your connection to the internet, and will generate a link for your friends to connect to after you click on "Open to LAN". 

# Installation/Quick Start
## Dependencies
The mod requires the following mods:
- [Fabric Loader & Fabric API](https://fabricmc.net/use/installer/)
- [ClothConfig API](https://www.curseforge.com/minecraft/mc-mods/cloth-config)
- [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu)

## Install Fabric Loader
1. Go to the [FabricMC website](https://fabricmc.net/use/installer/) and download the latest installer
2. Run the JAR, a window will pop up. Choose the correct version of Minecraft and loader (this mod currently requires Minecraft v1.18.2 and loader v0.13.3), and click on "Install".
3. A new game version and profile will be created in the launcher's menu, which you can now use to launch Fabric.

## Install Ngrok-LAN
4. Put the mod JAR (available under [Releases](https://github.com/Alujjdnd/Ngrok-LAN/releases)) into your "mods" folder, along with the ClothConfig API and ModMenu JARs. (The Fabric API JAR should already be in your mods folder from the previous section)
5. Run the newly installed profile from step 3, and Ngrok LAN should load

## Configuring Ngrok-LAN
In your Minecraft menu screen, a new "Mods" button should be visible, click on it. In the list of mods on the left hand side, find Ngrok LAN, and click on the sliders button on the top right corner, above the "issues" button. You should be presented with a few options:
- "Enabled?": This will enable/disable the mod, so you can configure whether the world is shared with Ngrok
- "Ngrok Auth Token": This is the field to input your Ngrok auth token
- "Region": This is where you can select the region of the Ngrok datacenter that you wish to use, use ones that are closest to you to minimise latency

## Starting Ngrok LAN
1. Make sure that Ngrok LAN is enabled, the auth token is valid, and the correct region is selected
2. Open a new singleplayer world, and click on Open to LAN
3. Configure the game mode and whether to allow cheats
4. Press on "Start LAN World"
5. The game may freeze for a moment (especially on your first time using the mod), but if all goes well, the message "Ngrok service initiated successfully" should appear in chat, with a link highlighted in yellow
6. The link is automatically copied to your clipboard, and can now be shared with your friends
7. Other people just need to put this as a server IP into their multiplayer list, and join the world

## Getting your Ngrok Auth Token
1. Go to https://ngrok.com/, and sign up for a new account
2. On the dashboard, on your left-hand side menu, click on Getting Started > Your Auth Token
3. Copy the auth token, and put this into Ngrok LAN _(don't share this token with other people)_

## Temp Docs
- Will hitch a bit for now, especially on first load.

## TODO
- See if can make process parallel, so that the "Open to LAN" button doesn't 
hitch
- Add common issues with Ngrok
- Add ModRinth and CurseForge integration
- Add meta description for CurseForge and ModMenu information

## License
This template is available under the GNU AGPL-3.0 license.
