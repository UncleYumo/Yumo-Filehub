package filehub.uncleyumo.cn.mainapplication

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MainApplicationTests {

	@Test
	fun contextLoads() {
	}

//	@Test
//	fun test() {
//		val rootPath = Paths.get("yumo-filehub-store").toString()
//		val resourceLoader = ClassLoader.getSystemClassLoader()
//		val path = resourceLoader.getResource(rootPath)?.path ?: "No such file or directory"
//
//		ColorPrinter.printlnCyanRed("Resource path: $path")
//	}

}
