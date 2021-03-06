package fr.alexpado.bots.cmb;

import fr.alexpado.bots.cmb.bot.DiscordBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
public class CrossoutMarketBotApplication {

    public static  CrossoutConfiguration config;
    private static boolean               noBot = false;

    public CrossoutMarketBotApplication(CrossoutConfiguration configurationProvider) {

        config = configurationProvider;

        if (!noBot) {
            DiscordBot bot = new DiscordBot(configurationProvider);
            try {
                bot.login();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println();
            System.out.println();
            System.out.println(" ====== YOUR RUNNING THIS WITHOUT THE DISCORD BOT ENABLED ======");
            System.out.println();
            System.out.println();
        }
    }

    public static void main(String[] args) {

        noBot = Arrays.asList(args).contains("--no-bot");
        SpringApplication.run(CrossoutMarketBotApplication.class, args);
    }

}
