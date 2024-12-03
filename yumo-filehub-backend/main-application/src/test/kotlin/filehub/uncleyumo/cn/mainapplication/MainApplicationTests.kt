package filehub.uncleyumo.cn.mainapplication

import cn.uncleyumo.utils.ColorPrinter
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.nio.file.Files
import java.nio.file.Paths

@SpringBootTest
class MainApplicationTests {

	@Test
	fun contextLoads() {
	}

	@Test
	fun test() {
		val rootPath = Paths.get("resources").toString()
		val resourceLoader = ClassLoader.getSystemClassLoader()
		val path = Paths.get(resourceLoader.getResources(rootPath).nextElement().toURI())
		ColorPrinter.printlnCyanRed("path: $path")
	}

}
