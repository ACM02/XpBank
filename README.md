![XpBank-logo](https://user-images.githubusercontent.com/45048893/223295905-f9cde65f-5e34-4661-a088-2c25b70a07a3.png)

This is a Spigot based xp banking plugin "XpBank" which is my first official public plugin, I hope you enjoy!
For more information about the plugin see the [spigot page](https://www.spigotmc.org/resources/xpbank.101132/) or [wiki](https://github.com/ACM02/XpBank/wiki)

# For Developers

If you wish to contribute or just build the code for yourself here is the relevant information:
- This plugin is built using [maven](https://maven.apache.org/)
  - To build you can do `mvn package`
  - To clean your environment do `mvn clean`
- Due to the way the soft dependency on [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) you may have to run the following command to have maven recognize the jar file: `mvn install:install-file -Dfile=lib/PlaceholderAPI-2.11.2.jar -DgroupId=com.example -DartifactId=placeholderAPI -Dversion=2.11.2 -Dpackaging=jar`
