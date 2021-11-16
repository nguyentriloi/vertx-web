package com.example.vertx_stock_broker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> LOG.error("Unhandled: ", error));
    vertx.deployVerticle(new MainVerticle(), ar -> {
      if (ar.failed()) {
        LOG.error("Failed to deploy: ", ar.cause());
        return;
      }
      LOG.info("Deployed {}!", MainVerticle.class.getName());
    });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    final Router restApi = Router.router(vertx);
    restApi.get("/assets").handler(context -> {
      final JsonArray response = new JsonArray();
      response.add(new JsonObject().put("symbol", "APC"))
          .add(new JsonObject().put("symbol", "APC"))
          .add(new JsonObject().put("symbol", "AAA"))
          .add(new JsonObject().put("symbol", "BBB"))
          .add(new JsonObject().put("symbol", "CCC"));
      LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
      context.response().end(response.toBuffer());
    });

    vertx.createHttpServer()
        .requestHandler(restApi)
        .exceptionHandler(error -> LOG.error("HTTP server error: ", error))
        .listen(8888, http -> {
          if (http.succeeded()) {
            startPromise.complete();
            LOG.info("HTTP server started on port 8888");
          } else {
            startPromise.fail(http.cause());
          }
        });
  }
}
