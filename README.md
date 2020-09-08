# AuroraHuntersBot
Java written Telegram Bot, which helps photographers catch northern lights. You may use a working instance of it using the following username: @aurorahunters_bot \
The bot significantly simplifies the northern lights prediction and serve photographers as a tool which helps to take good shots giving a better work conditions. As we all know, Telegram protocol can work even with 2G/gprs/EDGE internet, and almost all northern lights photospots are remote locations where good internet is more of an exception than a rule. So our bot can give to a photographer required for a prediction information, which other apps can't provide without good connection. 

<img src="../master/images/Screenshot_2020-08-17-00-11-31-283_org.telegram.messenger.jpg" width="200px"/> <img src="../master/images/Screenshot_2020-08-17-00-11-39-572_org.telegram.messenger.jpg" width="200px"/> <img src="../master/images/Screenshot_2020-08-17-00-14-00-133_org.telegram.messenger.jpg" width="200px"/> <img src="../master/images/Screenshot_2020-08-17-00-15-10-109_org.telegram.messenger.jpg" width="200px"/>

## About the project
This is one of my first projects written on pure Java. I made it totally for free and its purpose to learn the Java and have a good real project in my portfolio. The idea was to bring someone's idea to life using my new java coder skills. 
You can copy, paste, change it and whatever. 
## What this bot do?
The bot fetches JSON from NOAA servers (DSCOVR satellite), stores and subsequently processes the required data. This significantly simplifies the northern lights prediction. Bot fetches and process the following values: the north-south direction of the interplanetary magnetic field (Bz), solar wind speed and density. The bot can build charts, send notifications to users, has archive where user can request historical data, etc:
- Builds charts 
- Notifies subscribers in real time of a high probability of northern lights 
- Calculates the waiting time for the arrival of the parameters to the Earth (waiting time)
- It has an archive that can be used to request historical data from the database
- Allows displaying data in the time zone required by the user
- Has custom notification, time zone and archive settings for each user
- The time zone is adjusted by sending GPS tag to the bot

## Buld and run

### Prerequisites:

- Linux machine with 1+ Gb RAM (tested on RHEL/OEL/CentOS 7,8);
- PostgreSQL 10+ installed;
- java-1.8.0-openjdk or oracle Java 8 installed (both tested);
- maven installed; (yum install maven on CentOS 7-8)
- git installed; (yum install git on CentOS 7-8)

### Installation:

AuroraHunters telegramBot installation may take up to 15 minutes.

`ssh username@yourserver`

Once you have logged to the server, clone the project to desirable directory (I used my user home subdirectory):

`git clone https://github.com/erdees/AuroraHuntersBot.git `

When repository cloned, cd to project folder:

`cd AuroraHuntersBot/`

And perform build process:

`mvn clean install`

Project build may take up to 5 minutes depends on the Internet connection speed and server performance. When building will be finished you will see the following message:
```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:27 min
[INFO] Finished at: 2020-08-14T01:30:05+03:00
[INFO] ------------------------------------------------------------------------
```
It means that maven downloaded all dependences and already built .jar file. with our bot. To see .jar with bot, cd to target subdirectory which appears once build finished:

`cd target/`

You will se some files in the folder, including “AuroraTelegramBot-1.0-SNAPSHOT-jar-with-dependencies.jar”. This file is our bot. You may leave it in the same folder or move/copy to a different place depending on your preferences. In this example, I’ll leave it here. Bot has a configuration file, which should be completed before starting the bot:

`mv config.properties.example config.properties`

Edit configuration file with your parameters.

`vim config.properties`

And replace required example variables by yours:
```
db.host = jdbc:postgresql://127.0.0.1:5432/yourdbname
db.login = yourdbuser
db.password = yourdbpassword
bot.username = @botusername
bot.token = YOUR-TELEGRAM-APIKEY
bot.site = yoursite.com
```
You can change other parameters listed in the config.properties, if necessary. By default, all parameters has optimised values so you can keep defaults. \
After save and exit. Once it done, make sure, that PostgreSQL up and running and the database, its user and password created correctly. In case, if database is unavailable or its credentials is wrong, bot will not start and return an error. 

Almost forget! Make sere that you have created required database structure:

`psql -U username -d dbname < schema.sql`

Last thing we should do, is to make the bot automatically up and running when operating system starts. To do it, we need to create systemd .service file which you can copy and configure from root repository folder:

`cp Aurora.service /etc/systemd/system/`

Edit required parameters in the .service file.

`vim  /etc/systemd/system/Aurora.service`

Change the bot working directory where .jar file located, for example: 

`WorkingDirectory=/opt/bots/AuroraTelegramBot`

Once done, check java parameters with required quantity of RAM for working application. I recommend to configure it with 128Mb, but I have tested it with 32Mb and it worked without any problems. 128Mb is preferable to receive generated graphs faster. An example of this parameter:

`ExecStart=/bin/java -Xms128m -Xmx128m -jar AuroraTelegramBot-1.0-SNAPSHOT-jar-with-dependencies.jar`

If you have changed anything in .service file, please reload a configuration by using following command:

`systemctl daemon-reload`

To add the bot to autostart, use the command:

`systemctl enable Aurora`

and finally, to start the bot:

`systemctl start Aurora`

Now the bot up and running, you are beautiful. 

## Bot Usage

/start to start the bot; \
/stop to stop the bot; \
/info to see this message; \
/last to see last values from DSCOVR satellite; \
/history to get old DSCOVR values; \
/time_settings to change your timezone; \
/notif_on to enable notifications; \
/notif_off to disable notifications; \
/links to get useful links 

Additional /last submenu commands: 

latest density graph /graph_bz \
latest speed graph /graph_speed \
latest bz graph /graph_density \
all latest graphs /graph_all 

Additional /history submenu commands if archive date was configured: 

/history 2020-07-01 - to configure archive for 2020-07-01; 

Once it done, you will be able to see the following commands: 

/archive_text - to get text table; \
/archive_graph_bz - to get bz_gsm graph for required date; \
/archive_graph_speed - to get speed graph for required date; \
/archive_graph_density - to get density graph for required date; \
/archive_graph_all - to get all three graphs for required date.

To configure your timezone, just share your gps location with the bot. If it is not possible, you can use a command: 

/time_settings +03:00 

Where +03:00 - your local timezone. 
