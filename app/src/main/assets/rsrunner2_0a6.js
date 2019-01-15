var bot = new RiveScript({utf8: true});

async function getRsReply(code,user,input) {
    bot.setHandler("coffeescript", new RSCoffeeScript(bot));
    bot.stream(code);
    bot.sortReplies();
    var result = await bot.reply(user, input);
    river.reply(result);
}