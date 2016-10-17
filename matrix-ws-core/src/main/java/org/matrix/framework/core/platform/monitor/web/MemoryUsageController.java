package org.matrix.framework.core.platform.monitor.web;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.matrix.framework.core.platform.monitor.Pass;
import org.matrix.framework.core.platform.web.rest.MatrixRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemoryUsageController extends MatrixRestController {

    @RequestMapping(value = "/monitor/memory", produces = "text/plain", method = RequestMethod.GET)
    @ResponseBody
    @Pass
    public String memoryUsage(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        builder.append("Heap Usage:\n").append(heapUsage).append('\n');

        MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        builder.append("\nNonHeap Usage:\n").append(nonHeapMemoryUsage).append('\n');

        builder.append("\nMemory Pools:\n");
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean pool : pools) {
            builder.append(pool.getName()).append(" (").append(pool.getType()).append(")\n").append(pool.getUsage()).append('\n');
        }

        builder.append("\nLast Collection Usage:\n");
        for (MemoryPoolMXBean pool : pools) {
            builder.append(pool.getName()).append(" (").append(pool.getType()).append(")\n").append(pool.getCollectionUsage()).append('\n');
        }

        return builder.toString();
    }
}
