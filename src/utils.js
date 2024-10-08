export const formatDate = (dateArray) => {
    // Destructure the array
    const [year, month, day] = dateArray;

    // Create a new Date object
    // Note: we subtract 1 from the month to account for JavaScript's 0-indexing
    const date = new Date(year, month - 1, day);

    // Format the date as DD.MM.YYYY
    return date.toLocaleDateString('en-GB', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    }).replace(/\//g, '.');
    // const [year, month, day, hour, minute, second, nanosecond] = dateArray;
    //
    // // Create a new Date object
    // // Note: we subtract 1 from the month to account for JavaScript's 0-indexing
    // const date = new Date(year, month - 1, day, hour, minute, second);
    //
    // // Add milliseconds (nanoseconds / 1,000,000)
    // date.setMilliseconds(nanosecond / 1000000);
    //
    // // Format the date as ISO string and return only the part we need
    // return date.toLocaleString().slice(0, 20).replace('T', ' ');
};


export const createGameUrl = (game) => `https://t.me/${process.env.REACT_APP_TG_BOT_NAME}?start=game_${game.gameCode}`

export const createShareUrl = (url, text) => {
    const encodedUrl = encodeURIComponent(url);
    // Replace '+' with '%20' to ensure spaces are correctly encoded
    const encodedText = encodeURIComponent(text);
    return "https://t.me/share/url?url=" + encodedUrl + "&text=" + encodedText;
}