enum Color {
    Red,
    Blue,
    Green,
    RGB(u32, u32, u32),
}

fn main() {
    let mut color = Color::RGB(122, 17, 40);

    match color {
        Color::Red => println!("The color is Red!"),
        Color::Blue => {
        	println!("The color is Blue!");
        },
        Color::Green => println!("The color is Green!"),
        Color::RGB(r, g, b) => {
            println!("Chosen all the colors!");
        	println!("All the colors!");
        },
    }
}
