package com.github.kristofa.brave.jersey;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.http.ITServletContainer;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.github.kristofa.brave.servlet.BraveServletFilter;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import java.io.IOException;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ITJerseyServletTraceFilter extends ITServletContainer {

  @Path("")
  public static class TestResource {

    @GET
    @Path("foo")
    public Response get() {
      return Response.status(200).build();
    }

    @GET
    @Path("disconnect")
    public Response disconnect() throws IOException {
      throw new IOException();
    }
  }

  @Override
  public void init(ServletContextHandler handler, Brave brave, SpanNameProvider spanNameProvider) {

    // add a servlet for the test resource
    DefaultResourceConfig config = new DefaultResourceConfig(TestResource.class);
    handler.addServlet(new ServletHolder(new ServletContainer(config)), "/*");

    // add the trace filter
    BraveServletFilter filter = ServletTraceFilter.builder(brave)
        .spanNameProvider(spanNameProvider)
        .build();
    handler.addFilter(new FilterHolder(filter), "/*", EnumSet.of(DispatcherType.REQUEST));
  }
}
