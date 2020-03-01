using System.Collections.Generic;
using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.OpenApi.Models;

namespace Project.API.WebApi.Swagger
{
    internal static class Extensions
    {
        public static IServiceCollection AddCustomSwagger(this IServiceCollection services)
        {
            services.AddSwaggerGen(c =>
            {
                c.SwaggerDoc(AvailableDocuments.Ordering,
                    new OpenApiInfo { Title = "Drinks Ordering API", Version = "v1" });
                c.SwaggerDoc(AvailableDocuments.Servicing,
                    new OpenApiInfo { Title = "Drinks Servicing API", Version = "v1" });
            });

            return services;
        }

        public static IApplicationBuilder UseCustomSwagger(this IApplicationBuilder app)
        {
            app.UseSwagger(c =>
            {
                c.PreSerializeFilters.Add((swagger, httpReq) =>
                {
                    swagger.Servers = new List<OpenApiServer> {
                        new OpenApiServer { Url = $"{httpReq.Scheme}://{httpReq.Host.Value}" } };
                });
            });
            app.UseSwaggerUI(c =>
            {
                c.SwaggerEndpoint($"/swagger/{AvailableDocuments.Ordering}/swagger.json",
                    "Drinks Ordering API v1");
                c.SwaggerEndpoint($"/swagger/{AvailableDocuments.Servicing}/swagger.json",
                    "Drinks Service API v1");
            });

            return app;
        }
    }
}