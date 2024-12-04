package cn.uncleyumo.filehub.mainapplication

import cn.uncleyumo.utils.ColorPrinter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class MainApplication {
    @Value("\${yumo-filehub.config.base-url}")
    lateinit var baseUrl: String
}

fun main(args: Array<String>) {
    runApplication<MainApplication>(*args)
    // 在应用程序启动后再获取baseUrl
    val baseUrl = (runApplication<MainApplication>(*args) as MainApplication).baseUrl
    ColorPrinter.printlnCyanRed("Filehub is running at >> $baseUrl")
}
