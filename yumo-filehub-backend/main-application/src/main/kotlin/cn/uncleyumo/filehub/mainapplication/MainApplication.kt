package cn.uncleyumo.filehub.mainapplication

import cn.uncleyumo.utils.ColorPrinter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class MainApplication

fun main(args: Array<String>) {
	runApplication<cn.uncleyumo.filehub.mainapplication.MainApplication>(*args)
	ColorPrinter.printlnCyanRed("Filehub is running at >> http://localhost:49153")
}
