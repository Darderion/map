package com.github.darderion.mundaneassignmentpolice.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class RouterController {

	@RequestMapping(value = ["/{path:[^\\.]*}"])
	fun redirect() = "forward:/"
}
