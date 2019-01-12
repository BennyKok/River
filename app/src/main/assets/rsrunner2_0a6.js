var bot = new RiveScript({utf8: true});

async function getRsReply(code,user,input) {
    bot.stream(code);
    bot.sortReplies();
    var result = await bot.reply(user, input);
    river.reply(result);
}