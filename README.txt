Urushi NeoForge 1.21.1 Port
===========================

This repository is an unofficial NeoForge 1.21.1 port of Urushi, originally
created by iwaliner.

Status
------

- Target Minecraft version: 1.21.1
- Target loader: NeoForge
- Current port version: 1.21.1-6.6.3
- Local verification: `gradlew.bat build --no-daemon`

Client interaction acceptance
-----------------------------

- `scripts/runclient_interaction_gate.ps1` launches an unattended quick-play
  client and exercises the rice-ear to raw-rice, rice-cauldron cooking, and
  rice-ears advancement paths through real client block-use packets.
- The project-owned test code and launcher are tracked here. The local
  `minecraft-mod-testing` runtime, leases, and process guard remain machine
  local and are intentionally not part of this repository.

Links
-----

- Original CurseForge project: https://www.curseforge.com/minecraft/mc-mods/urushi-mod
- Original source: https://github.com/iwaliner/Urushi-MOD-1.20.1
- This port: https://github.com/crabsatellite/Urushi-MOD-1.21.1-Neoforge
