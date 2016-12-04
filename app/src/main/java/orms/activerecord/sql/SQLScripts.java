package orms.activerecord.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thanhbui on 2016/12/03.
 */

public class SQLScripts {

    public static List<String> getScripts() {
        List<String> scripts = new ArrayList<>();

        //Version v1
        scripts.add("DROP TABLE IF EXISTS `t_advice`;\n"+
                "/*!40101 SET @saved_cs_client     = @@character_set_client */;\n"+
                "/*!40101 SET character_set_client = utf8 */;\n"+
                "CREATE TABLE `t_advice` (\n"+
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n"+
                "  `dietician_id` int(11) NOT NULL,\n"+
                "  `title` varchar(255) COLLATE utf8_unicode_ci NOT NULL,\n"+
                "  `user_id` int(11) NOT NULL,\n"+
                "  `advice_kcal` text COLLATE utf8_unicode_ci,\n"+
                "  `advice_profile` text COLLATE utf8_unicode_ci,\n"+
                "  `advice_nutrition` text COLLATE utf8_unicode_ci,\n"+
                "  `advice_status` text COLLATE utf8_unicode_ci,\n"+
                "  `dt_start_date` datetime NOT NULL,\n"+
                "  `dt_end_date` datetime NOT NULL,\n"+
                "  `score` int(11) NOT NULL DEFAULT '0',\n"+
                "  `dt_send_date` datetime DEFAULT NULL,\n"+
                "  `dt_created` datetime NOT NULL,\n"+
                "  `dt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n"+
                "  `status` tinyint(2) DEFAULT '-1' COMMENT '-1: pendding | 0 : send | 1:deleted',\n"+
                "  PRIMARY KEY (`id`),\n"+
                "  KEY `FK_advice_manager_idx` (`dietician_id`),\n"+
                "  KEY `FK_advice` (`user_id`),\n"+
                "  KEY `t_advice_id_user_id_status` (`id`,`user_id`,`status`),\n"+
                "  KEY `t_advice_user_id_status_dt_start_date` (`user_id`,`status`,`dt_start_date`),\n"+
                "  KEY `t_advice_status` (`status`),\n"+
                "  CONSTRAINT `FK_advice` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,\n"+
                "  CONSTRAINT `FK_advice_manager` FOREIGN KEY (`dietician_id`) REFERENCES `t_manager` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION\n"+
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;");

        //Version 2
        scripts.add("DROP TABLE IF EXISTS `t_category`;\n" +
                "/*!40101 SET @saved_cs_client     = @@character_set_client */;\n" +
                "/*!40101 SET character_set_client = utf8 */;\n" +
                "CREATE TABLE `t_category` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `code` int(11) NOT NULL,\n" +
                "  `name` varchar(255) NOT NULL,\n" +
                "  `dt_created` datetime NOT NULL,\n" +
                "  `dt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "  `status` tinyint(2) DEFAULT '0',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `code` (`code`),\n" +
                "  KEY `t_category_status` (`status`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;");

        return scripts;
    }
}