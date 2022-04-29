
let port = 8081

const portIndex = process.argv.indexOf('--port')

if (portIndex != -1) {
	const portLine = process.argv[portIndex + 1]
	if (Number.isInteger(portLine)) {
		port = Number(portLine)
	}
}

console.log(`Port: ${port}`)

const fastify = require('fastify')()
const path = require('path')

fastify.register(require('fastify-static'), {
	root: path.join(__dirname, 'dist'),
	prefix: '/'
})

const start = async () => {
	try {
		await fastify.listen(port)
	} catch (err) {
		fastify.log.error(err)
		process.exit(1)
	}
}

start()
