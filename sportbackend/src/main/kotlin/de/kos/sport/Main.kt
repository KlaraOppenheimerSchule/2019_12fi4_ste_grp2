import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaParser
import graphql.servlet.SimpleGraphQLServlet
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import graphql.servlet.GraphQLServletListener.RequestCallback
import javax.servlet.http.Cookie
import graphql.servlet.GraphQLServletListener
import java.util.ArrayList



class Main {

    init {
        val schema = getSchema()
        val listeners = ArrayList<GraphQLServletListener>()
        val lstnr = object : GraphQLServletListener {

            override fun onRequest(request: HttpServletRequest, response: HttpServletResponse): RequestCallback {
                println("onRequest:" + request.requestURI)
                //TODO cookies here
                response.addCookie(Cookie("sample", "test"))

                return object : RequestCallback {

                    override fun onSuccess(request: HttpServletRequest?, response: HttpServletResponse?) {
                        println("onSuccess:" + request!!.requestURI)
                    }

                    override fun onError(
                        request: HttpServletRequest?,
                        response: HttpServletResponse?,
                        throwable: Throwable?
                    ) {
                        //TODO add some error handling here
                        println("onError:" + request!!.requestURI)
                    }

                    override fun onFinally(request: HttpServletRequest?, response: HttpServletResponse?) {
                        println("onFinally:" + request!!.requestURI)
                    }

                }
            }
        }
        listeners.add(lstnr)

        val servlet = SimpleGraphQLServlet(schema)

        GraphQLSchema.newSchema().
        listeners.forEach { servlet.addListener(it) }
    }
}