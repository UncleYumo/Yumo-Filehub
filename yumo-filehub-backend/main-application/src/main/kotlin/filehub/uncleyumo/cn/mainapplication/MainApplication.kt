package filehub.uncleyumo.cn.mainapplication

import cn.uncleyumo.utils.ColorPrinter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MainApplication

fun main(args: Array<String>) {
	runApplication<MainApplication>(*args)
	ColorPrinter.printCyanRed("Filehub is running at >> http://localhost:49153")
}
