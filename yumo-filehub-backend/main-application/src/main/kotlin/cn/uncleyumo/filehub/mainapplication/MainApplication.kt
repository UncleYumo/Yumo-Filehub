package cn.uncleyumo.filehub.mainapplication

import cn.uncleyumo.utils.ColorPrinter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class MainApplication(
    @Value("\${yumo-filehub.config.base-url}")
    val baseUrl: String
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        ColorPrinter.printlnCyanRed("Filehub is running at >> $baseUrl")
    }
}

fun main(args: Array<String>) {
    runApplication<MainApplication>(*args)
}