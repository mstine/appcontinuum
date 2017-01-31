package io.barinek.continuum.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.dataaccess.AllocationDataGateway
import io.barinek.continuum.models.AllocationInfo
import io.barinek.continuum.util.BasicHandler
import org.eclipse.jetty.server.Request
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AllocationController(val mapper: ObjectMapper, val gateway: AllocationDataGateway) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        post("/allocations", request, httpServletResponse) {
            val allocation = mapper.readValue(request.reader, AllocationInfo::class.java)
            val record = gateway.create(allocation.projectId, allocation.userId, allocation.firstDay, allocation.lastDay)
            mapper.writeValue(httpServletResponse.outputStream, AllocationInfo(record.id, record.projectId, record.userId, record.firstDay, record.lastDay, "allocation info"))
        }
        get("/allocations", request, httpServletResponse) {
            val projectId = request.getParameter("projectId")
            val list = gateway.findBy(projectId.toLong()).map { record ->
                AllocationInfo(record.id, record.projectId, record.userId, record.firstDay, record.lastDay, "allocation info")
            }
            mapper.writeValue(httpServletResponse.outputStream, list)
        }
    }
}