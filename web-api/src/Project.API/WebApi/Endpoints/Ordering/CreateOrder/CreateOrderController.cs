using System.Net.Mime;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ModelBinding;
using Project.API.Ordering.Application.OrderDetails;
using Project.API.Ordering.Application.OrderService;
using Project.API.Ordering.Domain.Users;
using Project.API.WebApi.Endpoints.Ordering.Shared;
using Project.API.WebApi.Endpoints.Shared;
using Project.API.WebApi.Swagger;

namespace Project.API.WebApi.Endpoints.Ordering.CreateOrder
{
    [ApiExplorerSettings(GroupName = AvailableDocuments.Ordering)]
    [Authorize(Roles = Role.Client)]
    public class CreateOrderController : ControllerBase
    {
        private readonly CreateOrderService orderService;
        private readonly IOrderDetailsRepository orderDetailsRepository;

        public CreateOrderController(
            CreateOrderService orderService,
            IOrderDetailsRepository orderDetailsRepository
        )
        {
            this.orderService = orderService;
            this.orderDetailsRepository = orderDetailsRepository;
        }

        [HttpPost("api/orders")]
        [Consumes(MediaTypeNames.Application.Json)]
        [Produces(MediaTypeNames.Application.Json)]
        [ProducesResponseType(typeof(ResponseWrapper<SingleOrder>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ProblemDetails), StatusCodes.Status500InternalServerError)]

        public async Task<ResponseWrapper<SingleOrder>> createNewOrder(
            [FromBody, BindRequired] CreateOrderDetails orderDetails)
        {
            var createdOrderId = await orderService.CreateNewOrder(
                this.DomainUser(),
                orderDetails.AsClientOrder()
            );
            var orderToReturn = (await orderDetailsRepository.OrderDetailsWithId(createdOrderId))!;

            return ResponseWrapper<SingleOrder>.From(SingleOrder.From(orderToReturn));
        }
    }
}