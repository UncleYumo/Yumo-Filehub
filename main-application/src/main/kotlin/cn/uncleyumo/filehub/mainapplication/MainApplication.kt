package cn.uncleyumo.filehub.mainapplication

import cn.uncleyumo.utils.ColorPrinter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MainApplication

fun main(args: Array<String>) {
	runApplication<MainApplication>(*args)
	ColorPrinter.printlnFontCyan("Yumo-FileHub is running, Index Page -> \thttp://localhost:49153/index.html")
}
